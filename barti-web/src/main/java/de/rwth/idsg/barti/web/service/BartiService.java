package de.rwth.idsg.barti.web.service;

import de.rwth.idsg.barti.db.exception.PendingUpdateException;
import de.rwth.idsg.barti.server.IncomingJob;
import de.rwth.idsg.barti.server.SamServer;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Writer;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Service
public class BartiService {
    private final SamServer samServer;

    @Autowired public BartiService(final SamServer samServer) {
        this.samServer = samServer;
    }

    public void process(final IncomingJob job) {
        samServer.addJob(job);
    }

    public void getTicketLog(final String apiToken, final YearMonth yearMonth, final Writer writer) throws
            PendingUpdateException {
        samServer.getDbCache().getDatabase().getLogLinesAsCSV(apiToken, yearMonth, writer);
    }
}
