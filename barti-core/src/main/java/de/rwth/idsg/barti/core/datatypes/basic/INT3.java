package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * INT3 ist ein 3 Byte großer, vorzeichenloser Integer.
 * Darstellbarer Wertebereich: 0 .. 16777215
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class INT3 implements Data<INT3> {

    public static final ReadDescription<INT3> READ_DESCRIPTION = stream -> new INT3(stream.read3ByteUnsigned());

    final int value;

    @Override
    public ReadDescription<INT3> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write3ByteUnsigned(value);
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public String toString() {
        return Long.toHexString(value).toUpperCase();
    }
}
