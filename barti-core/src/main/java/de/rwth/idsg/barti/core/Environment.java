package de.rwth.idsg.barti.core;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Environment {
    public static final Charset ISO_8859_15 = Charset.forName("ISO-8859-15");
    public static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;
}
