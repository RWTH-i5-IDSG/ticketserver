package de.rwth.idsg.barti.server;

import de.intarsys.security.smartcard.card.*;
import de.intarsys.security.smartcard.card.standard.StandardCardSystem;
import de.intarsys.security.smartcard.pcsc.PCSCContextFactory;
import de.intarsys.security.smartcard.pcsc.nativec._PCSC;
import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVCert;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVKey;
import de.rwth.idsg.barti.sam.EfSamID;
import de.rwth.idsg.barti.sam.communication.SamInfo;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.server.exception.SamIsBlacklistedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static de.rwth.idsg.barti.core.Util.asHexString;
import static de.rwth.idsg.barti.sam.communication.Authenticate.authenticate;
import static de.rwth.idsg.barti.sam.communication.Common.retry;
import static de.rwth.idsg.barti.sam.communication.ManageSecurityEnvironment.manageSecurityEnvironment;
import static de.rwth.idsg.barti.sam.communication.ReadRecord.*;
import static de.rwth.idsg.barti.sam.communication.SelectApplication.selectApplication;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class EventMonitor implements CardSystemMonitor.ICardSystemListener {
    private final Object lock = new Object();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    /**
     * contains marker object instances that are produced by card removal events and consumed by either the time-out
     * handler (which only executes in case the corresponding marker object is still in place, since the corresponding
     * marker object may have already been consumed and other events may have been placed in the map) or by the card
     * insertion handler, which can always just consume the marker event since removal and insertion events are always
     * alternating with respect to a single terminal
     */
    private final Map<String, Object> possibleHiccups = new HashMap<>();
    /**
     * contains stateful marker objects that are produced by card insertion events and consumed by either successful
     * initializations or by the card removal event handler which also sets the state of the marker to false to prevent
     * the relying initialization job from trying to work on a removed card
     */
    private final Map<String, AtomicBoolean> runningInitializations = new HashMap<>();
    private final Map<String, SamInfoAndWorker> terminalNameToSam = new HashMap<>();

    private final JobDispatcher jobDispatcher;
    private final DBCache dbCache;
    private final Blacklist blacklist = new Blacklist();

    public static CardSystemMonitor getCardSystemMonitor(final JobDispatcher jobDispatcher, final DBCache dbCache) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        final StandardCardSystem cardSystem = new StandardCardSystem(PCSCContextFactory.get());
        final CardSystemMonitor cardSystemMonitor = new CardSystemMonitor(cardSystem);
        cardSystemMonitor.addCardSystemListener(new EventMonitor(jobDispatcher, dbCache));
        return cardSystemMonitor;
    }

    @Override
    public void onCardChanged(final ICard card) {
        // ignored
    }

    @Override
    public void onCardRemoved(final ICard card) {
        log.debug("card removal detected!");
        synchronized (lock) {
            final String terminalName = card.getCardTerminal().getName();
            // it there is an initialization running on the terminal, kill it
            final AtomicBoolean initializationBarrier = runningInitializations.remove(terminalName);
            if (null != initializationBarrier) {
                initializationBarrier.set(false);
            }
            final SamInfoAndWorker samInfoAndWorker = terminalNameToSam.get(terminalName);
            // if there is no samInfoAndWorker the initialization of the card has not been completed when it was removed
            // thus the card was not published to the system
            if (null == samInfoAndWorker) {
                return;
            }
            // kill worker thread
            final Thread thread = samInfoAndWorker.getThread();
            thread.interrupt();
            try {
                thread.join();
            } catch (final InterruptedException e) {
                log.error(e.getMessage(), e);
                return;
            }
            // register card removed to event to be able to consume card added event
            final Object hiccupMarker = new Object();
            possibleHiccups.put(terminalName, hiccupMarker);
            // start timed removal of the worker
            scheduledExecutorService.schedule(() -> {
                synchronized (lock) {
                    final boolean hiccupMarkerStillPresent = possibleHiccups.remove(terminalName, hiccupMarker);
                    if (!hiccupMarkerStillPresent) {
                        log.debug("HICCUP: Hiccup marker not found, aborting removal task!");
                        return;
                    }
                    log.debug("HICCUP: Hiccup marker still present, unpublishing worker!");
                    final ReentrantLock jobDispatcherLock = jobDispatcher.getLock();
                    // lock the job dispatcher (since we will modify its registry maps)
                    jobDispatcherLock.lock();
                    try {
                        // remove the old worker
                        unpublishWorker(samInfoAndWorker, jobDispatcher.getInputQueue());
                    } finally {
                        // unlock the job dispatcher
                        jobDispatcherLock.unlock();
                    }
                }
            }, 400, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onCardInserted(final ICard card) {
        log.debug("card insertion detected!");
        synchronized (lock) {
            final String terminalName = card.getCardTerminal().getName();
            // determine whether this is a hiccup or fast-re-insert event
            final boolean isHiccup = possibleHiccups.remove(terminalName) != null;
            log.debug("HICCUP: handling card insert event {} hiccup", isHiccup ? "with" : "without");
            final AtomicBoolean initializationBarrier = new AtomicBoolean(true);
            runningInitializations.put(terminalName, initializationBarrier);
            scheduledExecutorService.execute(
                    () -> {
                        // initialize the card info object
                        final SamInfoAndWorker samInfoAndWorker = loopInitializeUntilSuccessful(card,
                                initializationBarrier);
                        if (null == samInfoAndWorker && !isHiccup) {
                            // initialization was interrupted by card removal, no worker removal pending
                            return;
                        }
                        // lock the job dispatcher (since we will modify its registry maps)
                        synchronized (lock) {
                            if (!initializationBarrier.get()) {
                                // card already removed again
                                log.debug("HICCUP: card removed during initialisation, aborting card insert handler");
                                return;
                            }
                            runningInitializations.remove(terminalName, initializationBarrier);
                            final ReentrantLock jobDispatcherLock = jobDispatcher.getLock();
                            jobDispatcherLock.lock();
                            try {
                                // if this is a hiccup / fast-re-insertion event remove the old worker
                                if (isHiccup) {
                                    final SamInfoAndWorker oldSamInfoAndWorker = terminalNameToSam.get(terminalName);
                                    unpublishWorker(oldSamInfoAndWorker,
                                            (samInfoAndWorker != null
                                                    && samHasSameCapabilities(
                                                    oldSamInfoAndWorker.getSamInfo(),
                                                    samInfoAndWorker.getSamInfo()))
                                                    ? samInfoAndWorker.getSamWorker().getWorkQueue()
                                                    : jobDispatcher.getInputQueue()
                                    );
                                }
                                if (null != samInfoAndWorker) {
                                    // create a new worker and make it known to the job dispatcher
                                    publishWorker(samInfoAndWorker);
                                    // start the new worker [this could also be done after unlocking the job dispatcher]
                                    samInfoAndWorker.getThread().start();
                                }
                            } finally {
                                // unlock the job dispatcher
                                jobDispatcherLock.unlock();
                            }
                        }
                    }
            );
        }
    }

    public void reportBrokenSAM(final ICard card) {
        synchronized (lock) {
            onCardRemoved(card);
            onCardInserted(card);
        }
    }

    @Nullable
    private SamInfoAndWorker loopInitializeUntilSuccessful(final ICard card, final AtomicBoolean active) {
        final PrimitiveIterator.OfInt delayInSecondsIterator = IntStream.concat(
                IntStream.of(0, 1, 1, 1, 2, 5, 10),
                IntStream.generate(() -> 15)
        ).iterator();
        // while still relevant
        while (active.get()) {
            try {
                return initialize(card);
            } catch (final Throwable e) {
                log.error(e.getMessage(), e);
            }
            try {
                TimeUnit.SECONDS.sleep(delayInSecondsIterator.nextInt());
            } catch (final InterruptedException e) {
                break;
            }
        }
        return null;
    }

    // has to be executed while holding our lock and the job dispatcher lock
    private void unpublishWorker(final SamInfoAndWorker samInfoAndWorker,
                                 final Collection<? super DispatchedJob> drainTarget) {
        assert Thread.holdsLock(lock);
        assert jobDispatcher.lock.isHeldByCurrentThread();
        terminalNameToSam.remove(samInfoAndWorker.getSamInfo().getConnection().getCardTerminal().getName(),
                samInfoAndWorker);
        jobDispatcher.removeSamWorker(samInfoAndWorker);
        // re-enqueue pending jobs
        final SamWorker samWorker = samInfoAndWorker.getSamWorker();
        final LinkedBlockingQueue<DispatchedJob> pendingJobs = samWorker.getWorkQueue();
        pendingJobs.drainTo(drainTarget);
        try {
            tryToKillConnection(samInfoAndWorker.getSamInfo().getConnection());
        } catch (final CardException e) {
            log.warn(e);
        }
    }

    // has to be executed while holding our lock and the job dispatcher lock
    private void publishWorker(final SamInfoAndWorker samInfoAndWorker) {
        assert Thread.holdsLock(lock);
        assert jobDispatcher.lock.isHeldByCurrentThread();
        terminalNameToSam.put(samInfoAndWorker.getSamInfo().getConnection().getCardTerminal().getName(),
                samInfoAndWorker);
        jobDispatcher.addSamWorker(samInfoAndWorker);
    }

    private boolean samHasSameCapabilities(final SamInfo oldSam, final SamInfo newSam) {
        return Arrays.equals(getKeyIdentifiers(oldSam), getKeyIdentifiers(newSam));
    }

    private int[] getKeyIdentifiers(final SamInfo samInfo) {
        return samInfo.getPvMKs().stream().mapToInt(ki ->
                ki.getOrgID().getValue().getValue() * 256 + ki.getKeyID().getValue().getValue()
        ).sorted().toArray();
    }

    @Override
    public void onCardTerminalConnected(final ICardTerminal terminal) {
        // ignored
    }

    @Override
    public void onCardTerminalDisconnected(final ICardTerminal terminal) {
        // ignored
    }

    @Nonnull
    private SamInfoAndWorker initialize(final ICard card)
            throws CardException, SamException, SamIsBlacklistedException {
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (final InterruptedException e) {
            log.debug(e);
        }
        log.debug("initialising card {} [card-state {}] in terminal {} [terminal-state {}]", card, card.getState(),
                card.getCardTerminal(), card.getCardTerminal().getState());
        final ICardConnection connection = card.connectExclusive(_PCSC.SCARD_PROTOCOL_Tx);
        try {
            log.debug("selecting application...");
            retry(() -> selectApplication(connection));
            log.debug("reading sam id...");
            final EfSamID efSamID = retry(() -> readSamID(connection));
            if (blacklist.isBlacklisted(efSamID)) {
                throw new SamIsBlacklistedException(efSamID.toString());
            }
            final BlacklistingFailureCounter blacklistingFailureCounter = blacklist.getFailureCounter(efSamID);
            log.debug("reading Betreiber CHR...");
            final BetreiberCHR betreiberCHR = retry(() -> readBetreiberCHR(connection));
            log.debug("reading PV master keys...");
            final List<KeyInfo> pvMKs = retry(() -> readPVMKs(connection));

            log.debug("selecting Betreiber key...");
            final byte[] betreiberCHRRaw = betreiberCHR.write();
            manageSecurityEnvironment(connection, betreiberCHRRaw);
            final RSAKeyParameters betreiberKey = dbCache.getBetreiberChrToKey().get(betreiberCHR);
            if (null == betreiberKey) {
                log.error("Missing betreiber key for {} [{}]", betreiberCHR, asHexString(betreiberCHRRaw));
                throw new IllegalArgumentException("Missing Betreiber Key!");
            }

            log.debug("starting authentication...");
            authenticate(connection, betreiberKey, betreiberCHRRaw);
            log.debug("reading SAM CERT SIG...");
            final SamCVCert samSigCert = readCertSig(connection);
            final SamCVKey samSigKey = samSigCert.getKey();

            final SamInfo samInfo = new SamInfo(connection, efSamID, betreiberCHR, pvMKs, samSigKey);
            final SamWorker samWorker = new SamWorker(samInfo, this::reportBrokenSAM, jobDispatcher::dispatch,
                    dbCache::logTicketCreation, blacklistingFailureCounter);
            final Thread thread = new Thread(samWorker);
            return new SamInfoAndWorker(samInfo, samWorker, thread);
        } catch (final Throwable throwable) {
            try {
                TimeUnit.MILLISECONDS.sleep(3000);
            } catch (final InterruptedException e) {
                log.debug(e);
            }
            if (connection.getCard().getState().isConnectedExclusive()) {
                tryToKillConnection(connection);
            }
            throw throwable;
        }
    }

    private static void tryToKillConnection(final ICardConnection connection) throws CardException {
        log.debug("enforcing close on {}", connection);
        log.debug("card state: {}", connection.getCard().getState());
        log.debug("connection {} is valid: {}", connection, connection.isValid());
        connection.close(ICardConnection.MODE_LEAVE_CARD);
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (final InterruptedException e) {
            log.debug(e);
        }
        log.debug("card state: {}", connection.getCard().getState());
        log.debug("connection {} is valid: {}", connection, connection.isValid());
    }
}
