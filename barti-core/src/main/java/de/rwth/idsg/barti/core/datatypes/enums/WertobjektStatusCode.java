package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-76
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum WertobjektStatusCode implements Data<WertobjektStatusCode> {
    KEINE_ANGABE_INITIALISIERT_KEINE_AUSGABETRANSAKTION_AUSGEFUEHRT(0, "keine Angabe/initialisiert/keine "
            + "Ausgabetransaktion ausgeführt"),
    AUSGEGEBEN_ENTSPERRT(7, "ausgegeben/entsperrt"),
    GESPERRT(19, "gesperrt"),
    ZURUECKGENOMMEN(5, "zurückgenommen");

    final INT1 value;
    final String stringRepresentation;

    WertobjektStatusCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static WertobjektStatusCode of(final INT1 value) {
        for (final WertobjektStatusCode terminalTypCode : values()) {
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

    public static final ReadDescription<WertobjektStatusCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<WertobjektStatusCode> getReadDescription() {
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
