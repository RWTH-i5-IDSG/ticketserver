package de.rwth.idsg.barti.db.repository;

import de.rwth.idsg.barti.core.aggregate.BetreiberKey;
import de.rwth.idsg.barti.core.aggregate.LogLine;
import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.db.PseudoSequence;
import de.rwth.idsg.barti.db.exception.PendingUpdateException;
import org.joda.time.YearMonth;

import java.io.Writer;
import java.util.List;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface BasicRepository {

    List<PseudoSequence> getAllTicketSequenceCountersForDeployment(final int deployment);

    long getNextTicketNumber(final PseudoSequence sequence);

    List<ProductConfiguration> getAllConfigurationsForDeployment(final int deployment);

    List<BetreiberKey> getBetreiberKeys();

    void insertLogLine(final LogLine logLine);

    void getLogLinesAsCSV(final String logApiToken, final YearMonth month, final Writer writer)
            throws PendingUpdateException;

    void sendHeartbeat(final int deployment);
}
