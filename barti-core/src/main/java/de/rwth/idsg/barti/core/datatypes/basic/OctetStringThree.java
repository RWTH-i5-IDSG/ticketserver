package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * OctetString ist eine Kette von Octets (Bytes). Im englischsprachigen Raum ist die Bezeichnung ByteArray üblich.
 * Die Anzahl der Octets (=Bytes) wird als Parameter N in der Form OctetString(N) angegeben.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class OctetStringThree implements Data {

    public static final ReadDescription<OctetStringThree> READ_DESCRIPTION = stream -> new OctetStringThree(
            stream.readBytes(3));

    final byte[] value;

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeBytes(value);
    }

    @Override
    public ReadDescription<OctetStringThree> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public int getLength() {
        return 3;
    }
}
