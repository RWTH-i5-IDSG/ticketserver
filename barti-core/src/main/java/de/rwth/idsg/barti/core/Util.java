package de.rwth.idsg.barti.core;

import de.rwth.idsg.barti.core.datatypes.basic.BCDString;
import de.rwth.idsg.barti.core.datatypes.basic.Datef;
import de.rwth.idsg.barti.core.datatypes.basic.JJMMTT;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.joda.time.LocalDate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
@UtilityClass
public class Util {
    public static LocalDate localDateFromJJMM(final byte[] array) {
        return new LocalDate(
                BCDString.fromComposed(array[0]).getDecimal() + 2000,
                BCDString.fromComposed(array[1]).getDecimal(),
                1
        );
    }

    public static LocalDate localDateFromJJMMTT(final byte[] array) {
        try {
            return JJMMTT.READ_DESCRIPTION.read(array).getDate();
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static LocalDate localDateFromJJJJMMTT(final byte[] array) {
        return new LocalDate(
                BCDString.fromComposed(array[0]).getDecimal() * 100
                        + BCDString.fromComposed(array[1]).getDecimal(),
                BCDString.fromComposed(array[2]).getDecimal(),
                BCDString.fromComposed(array[3]).getDecimal()
        );
    }

    public static Datef asDatef(final byte[] array) throws IOException {
        return Datef.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(array)));
    }

    public static String asHexString(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static byte[] getBytes(final ByteBuffer buffer, final int nBytes) {
        final byte[] bytes = new byte[nBytes];
        buffer.get(bytes);
        return bytes;
    }
}
