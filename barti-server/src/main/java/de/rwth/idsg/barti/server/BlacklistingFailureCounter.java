package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.sam.EfSamID;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@ToString
@RequiredArgsConstructor
public class BlacklistingFailureCounter {
    final Blacklist origin;
    final EfSamID efSamID;
    int failureCounter;

    public void signalSuccess() {
        failureCounter = 0;
    }

    public void signalFailure() {
        if (++failureCounter > Constants.MAX_REINIT_TRIES_PER_SAM) {
            origin.addSamToBlacklist(efSamID);
        }
    }
}
