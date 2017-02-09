package de.rwth.idsg.barti.server;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICard;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.rwth.idsg.barti.core.aggregate.LogLine;
import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.core.aggregate.STBParameters;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.sam.communication.SamInfo;
import de.rwth.idsg.barti.sam.exception.ConditionsOfUseNotSatisfiedException;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.server.exception.TooManyFailedAttemptsException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.jooq.exception.DataAccessException;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import static de.rwth.idsg.barti.sam.communication.SignEntitlement.signEntitlement;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@RequiredArgsConstructor
public class SamWorker implements Runnable {
    final SamInfo samInfo;
    final Consumer<ICard> brokenCardConsumer;
    final Consumer<ConfiguredJob> jobRescheduler;
    final Consumer<LogLine> ticketCreationLogger;
    final BlacklistingFailureCounter blacklistingFailureCounter;
    @Getter
    final LinkedBlockingQueue<DispatchedJob> workQueue = new LinkedBlockingQueue<>();
    private int failureCounter;

    @Override
    public void run() {
        while (true) {
            final DispatchedJob job;
            try {
                job = workQueue.take();
            } catch (final InterruptedException e) {
                return;
            }
            log.debug("job {} reached worker {}!", job.getTicketIdentifier(), samInfo.getEfSamID());
            final IncomingJob originalJob = job.getOriginalJob();
            final SignatureResultHandler signatureResultHandler = originalJob.getSignatureResultHandler();
            final ICardConnection connection = samInfo.getConnection();
            try {
                final ProductConfiguration productConfiguration = job.getProductConfiguration();
                final ReferenceNumberTwo pvOrgId = productConfiguration.getPv().getOrgId();
                final KeyInfo pvMK = samInfo.getKeyForPv(pvOrgId);
                final byte[] samSigKeyChr = samInfo.getSamSigKey().getChr().write();
                final long ticketNumber = job.getTicketNumber();
                final STBParameters parameters = originalJob.getParameters();
                final ByteBuffer signedEntitlement = signEntitlement(
                        connection,
                        pvMK,
                        samSigKeyChr,
                        ticketNumber,
                        parameters,
                        productConfiguration);
                final byte[] ticket = signedEntitlement.array();
                final LogLine logLine = new LogLine(LocalDateTime.now(DateTimeZone.UTC), parameters,
                        productConfiguration, ticketNumber, pvMK.getKeyVersion().getValue().getValue(),
                        samInfo.getEfSamID().toString(), samSigKeyChr, ticket);
                try {
                    ticketCreationLogger.accept(logLine);
                } catch (final DataAccessException e) {
                    log.fatal("Could not log creation of ticket {} caused by {}", logLine, e);
                    signatureResultHandler.onFailure(e);
                    return;
                }
                log.debug("job {} successful, sending to result handler", job.getTicketIdentifier());
                signatureResultHandler.onSuccess(ticket);
                failureCounter = 0;
                blacklistingFailureCounter.signalSuccess();
            } catch (final CardException | ConditionsOfUseNotSatisfiedException e) {
                // unrecoverable
                log.info("Unrecoverable error, handing sam to re-initialization process", e);
                handleFailedJob(job, signatureResultHandler, e);
                handleFailedSam(connection, blacklistingFailureCounter);
                return;
            } catch (final SamException e) {
                log.info("Sam exception occurred, re-scheduling job", e);
                handleFailedJob(job, signatureResultHandler, e);
                if (++failureCounter > Constants.MAX_SIGN_TRIES_PER_SAM) {
                    handleFailedSam(connection, blacklistingFailureCounter);
                    return;
                }
            } catch (final Throwable throwable) {
                signatureResultHandler.onFailure(throwable);
            }
        }
    }

    private void handleFailedSam(final ICardConnection connection,
                                 final BlacklistingFailureCounter blacklistingFailureCounter) {
        brokenCardConsumer.accept(connection.getCard());
        blacklistingFailureCounter.signalFailure();
    }

    private void handleFailedJob(final DispatchedJob job,
                                 final SignatureResultHandler signatureResultHandler,
                                 final Exception e) {
        if (++job.failureCounter > Constants.MAX_TRIES_PER_JOB) {
            signatureResultHandler.onFailure(new TooManyFailedAttemptsException(e));
        } else {
            // re-schedule
            jobRescheduler.accept(job);
        }
    }

    public void addJob(final DispatchedJob dispatchedJob) {
        final boolean inserted = workQueue.offer(dispatchedJob);
        if (!inserted) {
            throw new Error("Worker thread input queue capacity exceeded!");
        }
    }
}
