package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.BCDString;
import de.rwth.idsg.barti.core.datatypes.basic.PrintableStringTwo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
@ToString
public class CAR implements Data<CAR> {
    @Getter
    @RequiredArgsConstructor
    public enum ServiceIndicator {
        AUTHENTICATION((byte) 0), DIGITIAL_SIGNATURE((byte) 1), KEY_ENCRYPTION((byte) 2), DATA_ENCRYPTION((byte) 3),
        KEY_AGREEMENT((byte) 4);
        final byte value;
    }

    @Getter
    @RequiredArgsConstructor
    public enum DiscretionaryData {
        ROOT_CA_PRODUCTION((byte) 0), SUB_CA_PRODUCTION((byte) 1),
        ROOT_CA_TEST_LEVEL_1((byte) 2), SUB_CA_TEST_LEVEL_1((byte) 3),
        ROOT_CA_TEST_LEVEL_2((byte) 4), SUB_CA_TEST_LEVEL_2((byte) 5),
        ROOT_CA_INTERNAL_TEST((byte) 6), SUB_CA_INTERNAL_TEST((byte) 7),
        ROOT_CA_TEST_LEVEL_2_2((byte) 8), SUB_CA_TEST_LEVEL_2_2((byte) 9);
        final byte value;
    }

    public static final byte[] VDV = new byte[]{(byte) 0x56, (byte) 0x44, (byte) 0x56};
    public static final int CAR_LENGTH = 8;
    public static final ReadDescription<CAR> READ_DESCRIPTION = stream -> {
        final PrintableStringTwo region = PrintableStringTwo.READ_DESCRIPTION.read(stream);
        final byte[] vdv = stream.readBytes(3);
        if (!Arrays.equals(vdv, VDV)) {
            throw new IllegalStateException();
        }
        final byte twoNibbles = stream.read1ByteSigned();
        final ServiceIndicator serviceIndicator = getServiceIndicator((byte) ((twoNibbles >> 4) & 0xF));
        final DiscretionaryData discretionaryData = getDiscretionaryData((byte) (twoNibbles & 0xF));
        final BCDString caNummer = BCDString.READ_DESCRIPTION.read(stream);
        final BCDString jahr = BCDString.READ_DESCRIPTION.read(stream);
        return new CAR(region, serviceIndicator, discretionaryData, caNummer, jahr);
    };

    @Override public ReadDescription<CAR> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        region.write(stream);
        stream.writeBytes(VDV);
        final byte twoNibbles = (byte) (serviceIndicator.getValue() << 4 | discretionaryData.getValue());
        stream.writeByte(twoNibbles);
        caNummer.write(stream);
        jahr.write(stream);
    }

    @Override
    public int getLength() {
        return CAR_LENGTH;
    }

    final PrintableStringTwo region;
    final ServiceIndicator serviceIndicator;
    final DiscretionaryData discretionaryData;
    final BCDString caNummer;
    final BCDString jahr;

    private static ServiceIndicator getServiceIndicator(final byte b) {
        for (ServiceIndicator serviceIndicator : ServiceIndicator.values()) {
            if (serviceIndicator.getValue() == b) {
                return serviceIndicator;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static DiscretionaryData getDiscretionaryData(final byte b) {
        for (DiscretionaryData discretionaryData : DiscretionaryData.values()) {
            if (discretionaryData.getValue() == b) {
                return discretionaryData;
            }
        }
        throw new UnsupportedOperationException();
    }
}
