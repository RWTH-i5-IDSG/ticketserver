package de.rwth.idsg.barti.core.aggregate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString
public class Partner {
    final int id;
    final String name;

    public Partner(final int id, @Nonnull final String name) {
        this.id = id;
        this.name = name;
    }
}
