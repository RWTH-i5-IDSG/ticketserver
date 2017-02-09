package de.rwth.idsg.barti.db.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.rwth.idsg.barti.db.Util;
import de.rwth.idsg.barti.db.repository.BasicRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Component
@Log4j2
public class HeartbeatService {
    @Autowired private BasicRepository database;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @PostConstruct
    public void initialize() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("HeartBeat-%d").build();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, threadFactory);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                () -> database.sendHeartbeat(Util.DEPLOYMENT), 0L, 1L, TimeUnit.DAYS);
        log.info("Heartbeat scheduled!");
    }

    @PreDestroy
    public void cleanup() {
        try {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            log.error("Termination interrupted", e);
        } finally {
            if (!scheduledThreadPoolExecutor.isTerminated()) {
                log.warn("Killing non-finished tasks");
            }
            scheduledThreadPoolExecutor.shutdownNow();
        }
    }
}
