package de.rwth.idsg.barti.core.aggregate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.joda.time.LocalDateTime;

import javax.annotation.Nonnull;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class LogLine {
    @Nonnull
    final LocalDateTime logTime;
    @Nonnull
    final STBParameters parameters;
    @Nonnull
    final ProductConfiguration productConfiguration;
    final long ticketNumber;
    final int pvMkVersion;
    @Nonnull
    final String samId;
    @Nonnull
    final byte[] signKeyChr;
    @Nonnull
    final byte[] ticket;
}
