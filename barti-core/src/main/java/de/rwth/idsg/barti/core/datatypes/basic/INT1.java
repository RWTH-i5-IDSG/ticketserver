package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * INT1 ist ein 1 Byte großer, vorzeichenloser Integer.
 * Darstellbarer Wertebereich: 0 .. 255
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class INT1 implements Data {

    public static final ReadDescription<INT1> READ_DESCRIPTION = stream -> new INT1(stream.read1ByteUnsigned());

    final short value;

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write1ByteUnsigned(value);
    }

    @Override
    public ReadDescription<INT1> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public String toString() {
        return Long.toHexString(value).toUpperCase();
    }
}
