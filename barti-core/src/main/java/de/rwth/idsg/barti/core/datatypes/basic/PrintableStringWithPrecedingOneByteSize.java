package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class PrintableStringWithPrecedingOneByteSize implements Data<PrintableStringWithPrecedingOneByteSize> {
    public static final ReadDescription<PrintableStringWithPrecedingOneByteSize> READ_DESCRIPTION = stream -> {
        final short length = stream.read1ByteUnsigned();
        final char[] string = stream.readChars(length);
        return new PrintableStringWithPrecedingOneByteSize(string);
    };

    final char[] string;

    @Override
    public ReadDescription<PrintableStringWithPrecedingOneByteSize> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write1ByteUnsigned((short) string.length);
        stream.writeChars(string);
    }

    @Override
    public int getLength() {
        return 1 + string.length;
    }

    public String getAsString() {
        return String.valueOf(string);
    }
}
