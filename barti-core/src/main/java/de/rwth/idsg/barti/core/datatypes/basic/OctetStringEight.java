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
public class OctetStringEight implements Data {

    public static final ReadDescription<OctetStringEight> READ_DESCRIPTION = stream -> new OctetStringEight(
            stream.readBytes(8));

    final byte[] value;

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeBytes(value);
    }

    @Override
    public ReadDescription<OctetStringEight> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public int getLength() {
        return 8;
    }
}
