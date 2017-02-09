package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.MyOutputStream;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Fortlaufende Nummer, die es erlaubt, die hiermit nummerierten Elemente in eine eindeutige Reihenfolge zu bringen.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class SequenceNumberTwo implements Data<SequenceNumberTwo> {
    public static final ReadDescription<SequenceNumberTwo> READ_DESCRIPTION =
            stream -> new SequenceNumberTwo(INT2.READ_DESCRIPTION.read(stream));

    final INT2 value;

    public SequenceNumberTwo(final int value) {
        this(new INT2(value));
    }

    @Override
    public ReadDescription<SequenceNumberTwo> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        value.write(stream);
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
