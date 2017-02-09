package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Fortlaufende Nummer, die es erlaubt, die hiermit nummerierten Elemente in eine eindeutige Reihenfolge zu bringen.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class SequenceNumberFour implements Data<SequenceNumberFour> {
    public static final ReadDescription<SequenceNumberFour> READ_DESCRIPTION =
            stream -> new SequenceNumberFour(INT4.READ_DESCRIPTION.read(stream));

    final INT4 value;

    public SequenceNumberFour(final long value) {
        this(new INT4(value));
    }

    @Override
    public ReadDescription<SequenceNumberFour> getReadDescription() {
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
