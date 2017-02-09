package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.Getter;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public abstract class CVCert<C extends CHR<C>, K extends CVKey<C, K>, S extends CVCert<C, K, S>> implements Data<S> {
    public static final byte FIRST_TAG = (byte) 0x7F;
    public static final byte SECOND_TAG = (byte) 0x21;

    final int innerLength;
    final K key;
    final Signature signature;

    public CVCert(final MyInputStream stream, final ReadDescription<K> cvKeyRD) throws IOException {
        stream.readTag(FIRST_TAG);
        stream.readTag(SECOND_TAG);
        stream.readTag((byte) 0x82);
        innerLength = stream.read2ByteUnsigned();
        key = cvKeyRD.read(stream);
        signature = Signature.READ_DESCRIPTION.read(stream);
    }

    @Override
    public int getLength() {
        return innerLength + 5;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write1ByteSigned(FIRST_TAG);
        stream.write1ByteSigned(SECOND_TAG);
        stream.write1ByteSigned((byte) 0x82);
        stream.write2ByteUnsigned(innerLength);
        key.write(stream);
        signature.write(stream);
    }
}
