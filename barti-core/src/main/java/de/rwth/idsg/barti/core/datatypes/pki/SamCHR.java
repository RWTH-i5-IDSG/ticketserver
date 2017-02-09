package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT2;
import de.rwth.idsg.barti.core.datatypes.basic.INT3;
import de.rwth.idsg.barti.core.datatypes.basic.JJMM;
import de.rwth.idsg.barti.core.datatypes.basic.JJMMTT;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class SamCHR implements CHR<SamCHR> {
    final INT2 orgID;
    final JJMM eov;
    final JJMMTT bov;
    final INT2 orgIDSamAuthority;
    final INT3 samNummer;

    public static final ReadDescription<SamCHR> READ_DESCRIPTION = stream -> {
        final INT2 orgID = INT2.READ_DESCRIPTION.read(stream);
        final JJMM eov = JJMM.READ_DESCRIPTION.read(stream);
        final JJMMTT bov = JJMMTT.READ_DESCRIPTION.read(stream);
        final INT2 orgIDSamAuthority = INT2.READ_DESCRIPTION.read(stream);
        final INT3 samNummer = INT3.READ_DESCRIPTION.read(stream);
        return new SamCHR(orgID, eov, bov, orgIDSamAuthority, samNummer);
    };

    @Override public ReadDescription<SamCHR> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        orgID.write(stream);
        eov.write(stream);
        bov.write(stream);
        orgIDSamAuthority.write(stream);
        samNummer.write(stream);
    }

    /**
     * Returns the least significant 8 bytes of the CHR.
     *
     * @return the least significant 8 bytes of the CHR
     */
    public byte[] getIdSam() {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(8);
        try (final MyOutputStream stream = new MyOutputStream(bytes)) {
            bov.write(stream);
            orgIDSamAuthority.write(stream);
            samNummer.write(stream);
        } catch (final IOException e) {
            throw new IllegalStateException("What?");
        }
        return bytes.toByteArray();
    }

    /**
     * Returns the least significant 10 bytes of the CHR.
     *
     * @return the least significant 10 bytes of the CHR
     */
    public byte[] getSamId() {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(10);
        try (final MyOutputStream stream = new MyOutputStream(bytes)) {
            eov.write(stream);
            bov.write(stream);
            orgIDSamAuthority.write(stream);
            samNummer.write(stream);
        } catch (final IOException e) {
            throw new IllegalStateException("What?");
        }
        return bytes.toByteArray();
    }
}
