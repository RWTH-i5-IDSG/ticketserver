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
public class ReferenceNumberThree implements Data<ReferenceNumberThree> {
    public static final ReadDescription<ReferenceNumberThree> READ_DESCRIPTION =
            stream -> new ReferenceNumberThree(INT3.READ_DESCRIPTION.read(stream));

    final INT3 value;

    public ReferenceNumberThree(final int value) {
        this(new INT3(value));
    }

    @Override
    public ReadDescription<ReferenceNumberThree> getReadDescription() {
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
