package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.server.exception.UnknownApiTokenException;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@ToString
@Log4j2
@Component
public class ConfigurationManager implements Runnable {

    final LinkedBlockingQueue<IncomingJob> inputQueue = new LinkedBlockingQueue<>();

    @Autowired private JobDispatcher jobDispatcher;

    @Autowired private DBCache dbCache;

    public void addJob(final IncomingJob incomingJob) {
        final boolean inserted = inputQueue.offer(incomingJob);
        if (!inserted) {
            throw new Error("Configuration manager input queue capacity exceeded!");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                final IncomingJob incomingJob = inputQueue.take();
                try {
                    log.debug("configuring job {}", incomingJob);
                    final ConfiguredJob configuredJob = configureJob(incomingJob);
                    jobDispatcher.dispatch(configuredJob);
                } catch (final Throwable throwable) {
                    log.error(throwable);
                    incomingJob.getSignatureResultHandler().onFailure(throwable);
                }
            } catch (final InterruptedException e) {
                log.error(e);
                return;
            }
        }
    }

    private ConfiguredJob configureJob(final IncomingJob incomingJob) throws UnknownApiTokenException {
        final String apiToken = incomingJob.getApiToken();
        final ProductConfiguration productConfiguration = dbCache.getApiTokenToProductConfiguration().get(apiToken);
        if (null == productConfiguration) {
            // api token not recognised
            throw new UnknownApiTokenException("API Token " + incomingJob.getApiToken() + " unknown!");
        }
        // job configuration complete
        return new ConfiguredJob(incomingJob, productConfiguration);
    }
}
