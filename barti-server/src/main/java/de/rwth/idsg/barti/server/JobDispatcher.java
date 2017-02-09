package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.db.PseudoSequence;
import de.rwth.idsg.barti.server.exception.NoMatchingSamException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import static de.rwth.idsg.barti.core.Util.asHexString;
import static java.util.stream.Collectors.toSet;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@ToString
@Log4j2
@EqualsAndHashCode
@Component
public class JobDispatcher implements Runnable {

    final LinkedBlockingQueue<ConfiguredJob> inputQueue = new LinkedBlockingQueue<>();

    final Map<ReferenceNumberTwo, MutableLoadBalancer<SamInfoAndWorker>> pvOrgIdToWorkers = new HashMap<>();

    final ReentrantLock lock = new ReentrantLock();

    @Autowired private DBCache dbCache;

    @Override
    public void run() {
        while (true) {
            try {
                final ConfiguredJob configuredJob = inputQueue.take();
                try {
                    log.debug("dispatching job {}", configuredJob);
                    lock.lock();
                    try {
                        process(configuredJob);
                    } finally {
                        lock.unlock();
                    }
                } catch (final Throwable throwable) {
                    log.error(throwable);
                    configuredJob.getOriginalJob().getSignatureResultHandler().onFailure(throwable);
                }
            } catch (final InterruptedException e) {
                log.error(e);
                return;
            }
        }
    }

    private void process(final ConfiguredJob configuredJob) throws InterruptedException {
        assert lock.isHeldByCurrentThread();
        final ReferenceNumberTwo pvOrgId = configuredJob.getProductConfiguration().getPv().getOrgId();
        final MutableLoadBalancer<SamInfoAndWorker> loadBalancer = pvOrgIdToWorkers.get(pvOrgId);
        if (loadBalancer == null || !loadBalancer.hasNext()) {
            // no SAM can produce tickets for the specified PV
            log.fatal("No SAM found for PV {} api-token: {}",
                    pvOrgId.getValue(),
                    configuredJob.getOriginalJob().getApiToken()
            );
            configuredJob.getOriginalJob().getSignatureResultHandler().onFailure(
                    new NoMatchingSamException("No matching SAM found!"));
            return;
        }
        final DispatchedJob dispatchedJob = configuredJob.toDispatchedJob(() -> {
            final ReferenceNumberTwo kvpOrgId = configuredJob.getProductConfiguration().getKvp().getOrgId();
            final PseudoSequence sequence = dbCache.getKvpOrgIdToSequence().get(kvpOrgId);
            if (null == sequence) {
                log.fatal("No sequence configured for KVP {}!", kvpOrgId);
                // no sequence configured for KVP, this means the configuration is broken
                configuredJob.getOriginalJob().getSignatureResultHandler().onFailure(
                        new Error("No sequence configured for KVP " + kvpOrgId.toString() + "!"));
            }
            return dbCache.getNextTicketNumber(sequence);
        });

        final SamInfoAndWorker samInfoAndWorker = loadBalancer.next();
        final SamWorker samWorker = samInfoAndWorker.getSamWorker();
        log.debug("job {} dispatched to sam {}",
                dispatchedJob.getTicketNumber(),
                samInfoAndWorker.getSamInfo().getEfSamID());
        samWorker.addJob(dispatchedJob);
    }

    public void removeSamWorker(final SamInfoAndWorker samInfoAndWorker) {
        assert lock.isHeldByCurrentThread();
        final Set<ReferenceNumberTwo> pvOrgIds = samInfoAndWorker.getSamInfo().getPvMKs()
                .stream().map(KeyInfo::getOrgID).collect(toSet());
        for (ReferenceNumberTwo pvOrgId : pvOrgIds) {
            final MutableLoadBalancer<SamInfoAndWorker> loadBalancer = pvOrgIdToWorkers.get(pvOrgId);
            if (null == loadBalancer) {
                continue;
            }
            loadBalancer.remove(samInfoAndWorker);
        }
    }

    public void addSamWorker(final SamInfoAndWorker samInfoAndWorker) {
        assert lock.isHeldByCurrentThread();
        final Set<ReferenceNumberTwo> pvOrgIds = samInfoAndWorker.getSamInfo().getPvMKs()
                .stream().map(KeyInfo::getOrgID).collect(toSet());
        for (ReferenceNumberTwo pvOrgId : pvOrgIds) {
            final MutableLoadBalancer<SamInfoAndWorker> loadBalancer = pvOrgIdToWorkers.computeIfAbsent(pvOrgId,
                    x -> new MutableLoadBalancer<>());
            log.debug("adding sam for PV {}", asHexString(pvOrgId.write()));
            loadBalancer.add(samInfoAndWorker);
        }
    }

    public void dispatch(final ConfiguredJob configuredJob) {
        final boolean inserted = inputQueue.offer(configuredJob);
        if (!inserted) {
            throw new Error("Job dispatcher input queue capacity exceeded!");
        }
    }
}
