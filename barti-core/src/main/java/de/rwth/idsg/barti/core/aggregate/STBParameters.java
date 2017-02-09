package de.rwth.idsg.barti.core.aggregate;

import de.rwth.idsg.barti.core.datatypes.basic.DateTimeCompact;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.joda.time.LocalDateTime;

/**
 * Non-constant and non-configured part of the ticket.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class STBParameters {
    final String freitext;
    final DateTimeCompact berGueltigkeitsbeginn;
    final DateTimeCompact berGueltigkeitsende;

    public STBParameters(final String freitext, final LocalDateTime bov, final LocalDateTime eov) {
        this(freitext, new DateTimeCompact(bov), new DateTimeCompact(eov));
    }
}
