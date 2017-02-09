package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.sam.EfSamID;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Blacklist {
    private final Map<EfSamID, BlacklistingFailureCounter> blacklistCounters =
            Collections.synchronizedMap(new HashMap<>());
    private final Set<EfSamID> blacklistedSams = Collections.synchronizedSet(new HashSet<>());

    public BlacklistingFailureCounter getFailureCounter(final EfSamID efSamID) {
        return blacklistCounters.computeIfAbsent(efSamID, e -> new BlacklistingFailureCounter(this, efSamID));
    }

    public boolean isBlacklisted(final EfSamID efSamID) {
        return blacklistedSams.contains(efSamID);
    }

    public void addSamToBlacklist(final EfSamID efSamID) {
        this.blacklistedSams.add(efSamID);
    }
}
