package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-52
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum NmTransaktionsTypCode implements Data<NmTransaktionsTypCode> {
    AUSGABETRANSAKTION_APPLIKATION_BERECHTIGUNG(1, "Ausgabetransaktion Applikation/Berechtigung");

    final INT1 value;
    final String stringRepresentation;

    NmTransaktionsTypCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static NmTransaktionsTypCode of(final INT1 value) {
        for (final NmTransaktionsTypCode terminalTypCode : values()) {
            if (terminalTypCode.value.equals(value)) {
                return terminalTypCode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public static final ReadDescription<NmTransaktionsTypCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<NmTransaktionsTypCode> getReadDescription() {
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
