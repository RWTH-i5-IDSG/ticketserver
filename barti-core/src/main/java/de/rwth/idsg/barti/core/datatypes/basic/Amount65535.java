package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Amount65535 ist ein 2 Byte großer, vorzeichenloser Integer.
 * Darstellbarer Wertebereich: 0 .. 65535
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class Amount65535 implements Data<Amount65535> {

    public static final ReadDescription<Amount65535> READ_DESCRIPTION = stream -> new Amount65535(INT2
            .READ_DESCRIPTION.read(stream));

    final INT2 value;

    public Amount65535(final int value) {
        this(new INT2(value));
    }

    @Override
    public ReadDescription<Amount65535> getReadDescription() {
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
}
