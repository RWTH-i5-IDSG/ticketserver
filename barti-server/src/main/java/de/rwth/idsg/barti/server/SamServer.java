package de.rwth.idsg.barti.server;

import de.intarsys.security.smartcard.card.CardSystemMonitor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Component
@Log4j2
public class SamServer {

    private final JobDispatcher jobDispatcher;
    @Getter
    private final DBCache dbCache;
    private final ConfigurationManager configurationManager;

    private final Thread jbThread;
    private final Thread cmThread;
    private final CardSystemMonitor cardSystemMonitor;

    @Autowired public SamServer(JobDispatcher jobDispatcher, DBCache dbCache, ConfigurationManager
            configurationManager) {
        this.jobDispatcher = jobDispatcher;
        this.dbCache = dbCache;
        this.configurationManager = configurationManager;
        this.jbThread = run(jobDispatcher, "JobDispatcher");
        this.cmThread = run(configurationManager, "ConfigurationManager");
        this.cardSystemMonitor = EventMonitor.getCardSystemMonitor(jobDispatcher, dbCache);
        this.cardSystemMonitor.start();
    }

    private static Thread run(final Runnable runnable, final String name) {
        final Thread thread = new Thread(runnable, name);
        thread.start();
        return thread;
    }

    @PreDestroy
    public void cleanup() {
        cardSystemMonitor.stop();
        cmThread.interrupt();
        jbThread.interrupt();
    }

    public void addJob(final IncomingJob incomingJob) {
        configurationManager.addJob(incomingJob);
    }
}
