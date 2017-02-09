package de.rwth.idsg.barti.core.aggregate;

import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@EqualsAndHashCode
@ToString
public class BetreiberKey {
    @Nonnull
    final BetreiberCHR chr;
    @Nonnull
    final byte[] privateExponent;
    @Nonnull
    final byte[] modulus;

    public BetreiberKey(@Nonnull final BetreiberCHR chr,
                        @Nonnull final byte[] privateExponent,
                        @Nonnull final byte[] modulus) {
        this.chr = chr;
        this.privateExponent = privateExponent;
        this.modulus = modulus;
    }

    public BetreiberKey(@Nonnull final byte[] chr,
                        @Nonnull final byte[] privateExponent,
                        @Nonnull final byte[] modulus) throws IOException {
        this(BetreiberCHR.READ_DESCRIPTION.read(chr), privateExponent, modulus);
    }
}
