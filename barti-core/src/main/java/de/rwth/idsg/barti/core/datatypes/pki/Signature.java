package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
@ToString
public class Signature implements Data<Signature> {
    public static final byte FIRST_TAG = (byte) 0x5F;
    public static final byte SECOND_TAG = (byte) 0x37;

    final short innerLength;
    final byte[] data;

    public static final ReadDescription<Signature> READ_DESCRIPTION = stream -> {
        stream.readTag(FIRST_TAG);
        stream.readTag(SECOND_TAG);
        stream.readTag((byte) 0x81);
        final short innerLength = stream.read1ByteUnsigned();
        final byte[] data = stream.readBytes(innerLength);
        return new Signature(innerLength, data);
    };

    @Override
    public ReadDescription<Signature> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write1ByteSigned(FIRST_TAG);
        stream.write1ByteSigned(SECOND_TAG);
        stream.write1ByteSigned((byte) 0x81);
        stream.write1ByteUnsigned(innerLength);
        stream.writeBytes(data);
    }

    @Override
    public int getLength() {
        return innerLength + 4;
    }
}
