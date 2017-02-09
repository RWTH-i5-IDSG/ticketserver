package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class PrintableStringTwo implements Data<PrintableStringTwo> {
    public static final int LENGTH = 2;
    public static final ReadDescription<PrintableStringTwo> READ_DESCRIPTION = stream -> {
        final char[] string = stream.readChars(LENGTH);
        return new PrintableStringTwo(string);
    };

    final char[] string;

    public PrintableStringTwo(final char[] string) {
        if (string.length != LENGTH) {
            throw new IllegalArgumentException();
        }
        this.string = string;
    }

    @Override public ReadDescription<PrintableStringTwo> getReadDescription() {
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
