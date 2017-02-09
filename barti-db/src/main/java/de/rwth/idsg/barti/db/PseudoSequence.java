package de.rwth.idsg.barti.db;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class PseudoSequence {
    final int kvpOrgId;
    final int deploymentId;
    @Nonnull
    final AtomicLong currentValue;
    final long minValue;
    final long maxValue;
}
