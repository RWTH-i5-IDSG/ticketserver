package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * INT2 ist ein 2 Byte großer, vorzeichenloser Integer.
 * Darstellbarer Wertebereich: 0 .. 65535
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class INT2 implements Data<INT2> {

    public static final ReadDescription<INT2> READ_DESCRIPTION = stream -> new INT2(stream.read2ByteUnsigned());

    final int value;

    @Override
    public ReadDescription<INT2> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write2ByteUnsigned(value);
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public String toString() {
        return Long.toHexString(value).toUpperCase();
    }
}
