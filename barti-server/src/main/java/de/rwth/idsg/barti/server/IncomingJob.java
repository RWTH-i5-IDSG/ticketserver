package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.core.aggregate.STBParameters;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class IncomingJob {
    final String apiToken;
    final STBParameters parameters;
    final SignatureResultHandler signatureResultHandler;
}
