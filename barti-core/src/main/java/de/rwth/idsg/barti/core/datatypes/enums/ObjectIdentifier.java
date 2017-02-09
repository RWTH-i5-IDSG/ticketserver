package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 4.3.1.6 SAM Spec
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum ObjectIdentifier implements Data<ObjectIdentifier> {
    SIGNATURE_APPENDED(new byte[]{(byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D,
            (byte) 0x01, (byte) 0x01, (byte) 0x05}),
    SIGNATURE_RSASSA_PSS(new byte[]{(byte) 0x2A, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D,
            (byte) 0x01, (byte) 0x01, (byte) 0x0A}),
    SIGNATURE_9796D2S1(new byte[]{(byte) 0x2B, (byte) 0x24, (byte) 0x03, (byte) 0x04, (byte) 0x02, (byte) 0x02,
            (byte) 0x01}),
    AUTHENTICATION_9796D2(new byte[]{(byte) 0x2B, (byte) 0x24, (byte) 0x03, (byte) 0x05, (byte) 0x02, (byte) 0x02,
            (byte) 0x01}),
    ENCRYPTION(new byte[]{(byte) 0x2B, (byte) 0x24, (byte) 0x07, (byte) 0x02, (byte) 0x01, (byte) 0x01});

    final byte[] value;

    public static final ReadDescription<ObjectIdentifier> READ_DESCRIPTION = stream -> {
        final byte[] firstSix = stream.readBytes(6);
        if (equalInRange(firstSix, ENCRYPTION.value, 6)) {
            return ENCRYPTION;
        }
        if (equalInRange(firstSix, SIGNATURE_APPENDED.value, 6)) {
            final byte[] lastThree = stream.readBytes(3);
            if (equalInRange(lastThree, 0, SIGNATURE_APPENDED.value, 6, 3)) {
                return SIGNATURE_APPENDED;
            }
            if (equalInRange(lastThree, 0, SIGNATURE_RSASSA_PSS.value, 6, 3)) {
                return SIGNATURE_RSASSA_PSS;
            }
        }
        if (equalInRange(firstSix, SIGNATURE_9796D2S1.value, 6)) {
            final byte next = stream.read1ByteSigned();
            if (next == SIGNATURE_9796D2S1.value[6]) {
                return SIGNATURE_9796D2S1;
            }
        }
        if (equalInRange(firstSix, AUTHENTICATION_9796D2.value, 6)) {
            final byte next = stream.read1ByteSigned();
            if (next == AUTHENTICATION_9796D2.value[6]) {
                return AUTHENTICATION_9796D2;
            }
        }
        throw new IllegalArgumentException();
    };

    private static boolean equalInRange(final byte[] a, final byte[] b, final int limit) {
        return equalInRange(a, 0, b, 0, limit);
    }

    private static boolean equalInRange(final byte[] a, final int aOffset, final byte[] b, final int bOffset, final
    int limit) {
        for (int i = 0; i < limit; ++i) {
            if (a[i + aOffset] != b[i + bOffset]) {
                return false;
            }
        }
        return true;
    }

    @Override public ReadDescription<ObjectIdentifier> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeBytes(value);
    }

    @Override
    public int getLength() {
        return value.length;
    }
}
