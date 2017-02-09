package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class PrintableStringEight implements Data<PrintableStringEight> {
    public static final int LENGTH = 8;
    public static final ReadDescription<PrintableStringEight> READ_DESCRIPTION = stream -> {
        final char[] string = stream.readChars(LENGTH);
        return new PrintableStringEight(string);
    };

    final char[] string;

    public PrintableStringEight(final char[] string) {
        if (string.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        this.string = string;
    }

    @Override public ReadDescription<PrintableStringEight> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeChars(string);
    }

    @Override
    public int getLength() {
        return LENGTH;
    }
}
