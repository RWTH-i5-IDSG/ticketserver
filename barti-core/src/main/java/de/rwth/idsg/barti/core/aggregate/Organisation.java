package de.rwth.idsg.barti.core.aggregate;

import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
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
public class Organisation {
    @Nonnull
    final ReferenceNumberTwo orgId;
    @Nonnull
    final String name;

    public Organisation(@Nonnull final ReferenceNumberTwo orgId, @Nonnull final String name) {
        this.orgId = orgId;
        this.name = name;
    }

    public Organisation(final int orgId, @Nonnull final String name) {
        this(new ReferenceNumberTwo(orgId), name);
    }

    public int getOrgIdAsInt() {
        return orgId.getValue().getValue();
    }
}
