package de.rwth.idsg.barti.server;

import de.rwth.idsg.barti.sam.communication.SamInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class SamInfoAndWorker {
    @Nonnull
    final SamInfo samInfo;
    @Nonnull
    final SamWorker samWorker;
    @Nonnull
    final Thread thread;
}
