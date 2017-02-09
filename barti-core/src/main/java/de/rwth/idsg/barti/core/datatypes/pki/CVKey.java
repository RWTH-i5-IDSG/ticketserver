package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.Datef;
import de.rwth.idsg.barti.core.datatypes.enums.ObjectIdentifier;
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
public abstract class CVKey<C extends CHR<C>, T extends CVKey<C, T>> implements Data<T> {
    final int innerLength;
    // 1 byte certificate profile identifier
    final byte cpi;
    // 8 byte Certification Authority Reference
    final CAR car;
    // 12 byte Certificate Holder Reference
    final C chr;
    // 7 byte Certificate Holder Authorisation
    final CHA cha;
    // 4 byte End of validity (JJ JJ MM TT)
    final Datef eov;
    // 6-9 byte Object identifier
    final ObjectIdentifier oid;
    // 128-248 byte (=1024-1984 bit) modulus
    final byte[] modulus;
    // 4 byte exponent
    final byte[] exponent;

    public static final byte FIRST_TAG = (byte) 0x5F;
    public static final byte SECOND_TAG = (byte) 0x4E;
    public static final int EXPONENT_BYTES = 4;

    protected CVKey(final MyInputStream stream, final ReadDescription<C> chrRD) throws IOException {
        stream.readTag(FIRST_TAG);
        stream.readTag(SECOND_TAG);
        final byte lengthTag = stream.read1ByteSigned();
        if (lengthTag == (byte) 0x81) {
            innerLength = stream.read1ByteUnsigned();
        } else {
            innerLength = stream.read2ByteUnsigned();
        }
        cpi = stream.read1ByteSigned();
        final int modulusLength;
        switch (cpi) {
            case 3:
                modulusLength = 1536 / 8;
                break;
            case 4:
            case 5:
            case 6:
                modulusLength = 1024 / 8;
                break;
            case 7:
                modulusLength = 1984 / 8;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        car = CAR.READ_DESCRIPTION.read(stream);
        chr = chrRD.read(stream);
        cha = CHA.READ_DESCRIPTION.read(stream);
        eov = Datef.READ_DESCRIPTION.read(stream);
        oid = ObjectIdentifier.READ_DESCRIPTION.read(stream);
        modulus = stream.readBytes(modulusLength);
        exponent = stream.readBytes(EXPONENT_BYTES);
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeByte(FIRST_TAG);
        stream.writeByte(SECOND_TAG);
        if (innerLength <= 255) {
            stream.writeByte((byte) 0x81);
            stream.write1ByteUnsigned((short) innerLength);
        } else {
            stream.writeByte((byte) 0x82);
            stream.write2ByteUnsigned(innerLength);
        }
        stream.writeByte(cpi);
        car.write(stream);
        chr.write(stream);
        cha.write(stream);
        eov.write(stream);
        oid.write(stream);
        stream.writeBytes(modulus);
        stream.writeBytes(exponent);
    }

    @Override
    public int getLength() {
        return innerLength + 3 + (innerLength > 255 ? 2 : 1);
    }
}
