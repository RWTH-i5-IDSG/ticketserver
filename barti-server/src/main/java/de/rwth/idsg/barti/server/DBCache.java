package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.aggregate.BetreiberKey;
import de.rwth.idsg.barti.core.aggregate.LogLine;
import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import de.rwth.idsg.barti.db.PseudoSequence;
import de.rwth.idsg.barti.db.repository.BasicRepository;
import de.rwth.idsg.barti.sam.Util;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Component
@Log4j2
@Getter
public class DBCache {
    @Autowired private BasicRepository database;
    private final Map<ReferenceNumberTwo, PseudoSequence> kvpOrgIdToSequence = new HashMap<>();
    private final Map<BetreiberCHR, RSAKeyParameters> betreiberChrToKey = new HashMap<>();
    private final Map<String, ProductConfiguration> apiTokenToProductConfiguration = new HashMap<>();

    public long getNextTicketNumber(final PseudoSequence sequence) {
        return database.getNextTicketNumber(sequence);
    }

    @PostConstruct
    public void initialize() {
        final int deployment = de.rwth.idsg.barti.db.Util.DEPLOYMENT;
        for (final PseudoSequence sequence : database.getAllTicketSequenceCountersForDeployment(deployment)) {
            final ReferenceNumberTwo kvpOrgId = new ReferenceNumberTwo(sequence.getKvpOrgId());
            kvpOrgIdToSequence.put(kvpOrgId, sequence);
        }
        for (final ProductConfiguration configuration : database.getAllConfigurationsForDeployment(deployment)) {
            final String apiToken = configuration.getApiToken();
            apiTokenToProductConfiguration.put(apiToken, configuration);
        }
        for (final BetreiberKey betreiberKey : database.getBetreiberKeys()) {
            final BetreiberCHR betreiberCHR = betreiberKey.getChr();
            betreiberChrToKey.put(betreiberCHR, new RSAKeyParameters(true,
                    Util.toBigInt(betreiberKey.getModulus()),
                    Util.toBigInt(betreiberKey.getPrivateExponent())
            ));
        }
    }

    public void logTicketCreation(final LogLine logLine) {
        database.insertLogLine(logLine);
    }
}
