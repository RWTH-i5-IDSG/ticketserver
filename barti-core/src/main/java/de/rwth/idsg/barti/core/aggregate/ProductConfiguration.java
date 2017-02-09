package de.rwth.idsg.barti.core.aggregate;

import de.rwth.idsg.barti.core.datatypes.basic.*;
import de.rwth.idsg.barti.core.datatypes.enums.TransportmittelKategorieCode;
import lombok.Builder;
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
@Builder
public class ProductConfiguration {
    final int deploymentId;
    @Nonnull
    final ReferenceNumberTwo productId;
    @Nonnull
    final Organisation pv;
    @Nonnull
    final Organisation kvp;
    @Nonnull
    final Organisation transactionOp;
    @Nonnull
    final Organisation terminalOrg;
    @Nonnull
    final Organisation locationOrg;
    @Nonnull
    final ReferenceNumberTwo terminalNumber;
    @Nonnull
    final ReferenceNumberThree locationNumber;
    @Nonnull
    final TransportmittelKategorieCode meansOfTransportCategory;
    @Nonnull
    final Partner partner;
    @Nonnull
    final String apiToken;
}
