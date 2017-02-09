package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class PrintableStringForty implements Data<PrintableStringForty> {
    public static final int LENGTH = 40;
    public static final ReadDescription<PrintableStringForty> READ_DESCRIPTION = stream -> {
        final char[] string = stream.readChars(LENGTH);
        return new PrintableStringForty(string);
    };

    final char[] string;

    public PrintableStringForty(final char[] string) {
        if (string.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        this.string = string;
    }

    @Override
    public ReadDescription<PrintableStringForty> getReadDescription() {
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
