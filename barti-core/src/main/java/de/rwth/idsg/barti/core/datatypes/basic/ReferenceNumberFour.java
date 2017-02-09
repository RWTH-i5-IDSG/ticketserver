package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Eindeutige Nummer, die als Referenz (zur Identifizierung) verwendet werden kann.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class ReferenceNumberFour implements Data<ReferenceNumberFour> {
    public static final ReadDescription<ReferenceNumberFour> READ_DESCRIPTION =
            stream -> new ReferenceNumberFour(INT4.READ_DESCRIPTION.read(stream));

    final INT4 value;

    public ReferenceNumberFour(final long value) {
        this(new INT4(value));
    }

    @Override
    public ReadDescription<ReferenceNumberFour> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        value.write(stream);
    }

    @Override
    public int getLength() {
        return value.getLength();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
