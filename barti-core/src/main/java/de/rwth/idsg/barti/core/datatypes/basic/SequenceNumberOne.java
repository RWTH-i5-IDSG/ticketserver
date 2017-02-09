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
public class SequenceNumberOne implements Data<SequenceNumberOne> {
    public static final ReadDescription<SequenceNumberOne> READ_DESCRIPTION =
            stream -> new SequenceNumberOne(INT1.READ_DESCRIPTION.read(stream));

    final INT1 value;

    public SequenceNumberOne(final short value) {
        this(new INT1(value));
    }

    @Override
    public ReadDescription<SequenceNumberOne> getReadDescription() {
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
