package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-55
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum OrtsTypCode implements Data<OrtsTypCode> {
    BUSHALTESTELLE(0, "Bushaltestelle"),
    UBAHN_METRO_STATION(1, "U-Bahn- (Metho-)Station"),
    BAHNHOF_EISENBAHN(2, "Bahnhof (Eisenbahn)"),
    STRASSENBAHN_TRAM_HALTESTELLE(3, "Straßenbahn- (TRAM-) Haltestelle"),
    VERKAUFSSTELLE(11, "Verkaufsstelle"),
    GEBIET_ZONE(16, "Gebiet/Zone"),
    KORRIDOR(17, "Korridor"),
    HALTESTELLE_ALLGEMEIN(200, "Haltestelle allgemein"),
    MASSENPERSONALISIERER(201, "Massenpersonalisierer"),
    AREALISTID(202, "areaList_ID"),
    IM_FAHRZEUG_ZUG(203, "im Fahrzeug/Zug"),
    TOUCHPOINT(204, "Touchpoint"),
    IM_FAHRZEUG_DER_LINIE(205, "im Fahrzeug der Linie"),
    IM_FAHRZEUG_DER_ZUGNUMMER(206, "im Fahrzeug der Zugnummer"),
    TEILZONE(207, "Teilzone"),
    NEUTRALE_ZONE(208, "neutrale Zone"),
    IM_FAHRZEUG_AN_HALTESTELLE(213, "im Fahrzeug an Haltestelle"),
    EREIGNISORT(214, "Ereignisort (event location)"),
    TICKETSERVER(215, "Ticketserver"),
    ORTSTEIL(251, "Ortsteil"),
    GEMEINDE(252, "Gemeinde"),
    KREIS(253, "Kreis"),
    LAND(254, "Land"),
    KEINE_ANGABE(255, "keine Angabe");

    final INT1 value;
    final String stringRepresentation;

    OrtsTypCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static OrtsTypCode of(final INT1 value) {
        for (final OrtsTypCode terminalTypCode : values()) {
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

    public static final ReadDescription<OrtsTypCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<OrtsTypCode> getReadDescription() {
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
