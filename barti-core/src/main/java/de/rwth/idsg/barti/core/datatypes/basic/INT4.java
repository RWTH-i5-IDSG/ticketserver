package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * INT4 ist ein 4 Byte großer, vorzeichenloser Integer.
 * Darstellbarer Wertebereich: 0 .. 4294967295
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class INT4 implements Data<INT4> {

    public static final ReadDescription<INT4> READ_DESCRIPTION = stream -> new INT4(stream.read4ByteUnsigned());

    final long value;

    @Override
    public ReadDescription<INT4> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write4ByteUnsigned(value);
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public String toString() {
        return Long.toHexString(value).toUpperCase();
    }
}
