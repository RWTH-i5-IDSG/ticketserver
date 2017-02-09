package de.rwth.idsg.barti.sam;

import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.joda.time.LocalDate;

import java.nio.ByteBuffer;

import static de.rwth.idsg.barti.core.Util.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
@Log4j2
public class EfSamID {
    // 2 byte JJMM
    byte[] samVerfallsdatum;
    // 3 byte JJMMTT
    byte[] samGueltigkeitsbeginn;
    // 2 byte OrgID
    byte[] orgID;
    // 3 byte Modulspezifische Nummer, SAM_NR
    byte[] samNr;

    public static EfSamID fromBytes(final byte[] bytes) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final byte tag = byteBuffer.get();
        if (tag != 0x5A) {
            log.debug(de.rwth.idsg.barti.core.Util.asHexString(bytes));
            throw new IllegalArgumentException();
        }
        final byte length = byteBuffer.get();
        if (length != 0x0A) {
            throw new IllegalStateException();
        }
        // 2 byte JJMM
        final byte[] samVerfallsdatum = new byte[2];
        byteBuffer.get(samVerfallsdatum);
        // 3 byte JJMMTT
        final byte[] samGueltigkeitsbeginn = new byte[3];
        byteBuffer.get(samGueltigkeitsbeginn);
        // 2 byte OrgID
        final byte[] orgID = new byte[2];
        byteBuffer.get(orgID);
        // 3 byte Modulspezifische Nummer, SAM_NR
        final byte[] samNr = new byte[3];
        byteBuffer.get(samNr);
        return new EfSamID(samVerfallsdatum, samGueltigkeitsbeginn, orgID, samNr);
    }

    public LocalDate getVerfallsdatum() {
        return localDateFromJJMM(samVerfallsdatum);
    }

    public LocalDate getGueltigkeitsbeginn() {
        return localDateFromJJMMTT(samGueltigkeitsbeginn);
    }

    @Override
    public String toString() {
        return "EF_SAM_ID( "
                + "Verfallsdatum: " + getVerfallsdatum() + ", "
                + "Gueltigkeitsbeginn: " + getGueltigkeitsbeginn() + ", "
                + "OrgID: " + asHexString(this.orgID) + ", "
                + "SAM Nr: " + asHexString(this.samNr) + ")";
    }
}
