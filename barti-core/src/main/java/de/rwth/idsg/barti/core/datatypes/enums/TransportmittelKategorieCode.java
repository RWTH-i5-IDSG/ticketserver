package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-71
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum TransportmittelKategorieCode implements Data<TransportmittelKategorieCode> {
    NICHT_SPEZIFIZIERT_UNBESTIMMT(0, "nicht spezifiziert/unbestimmt"),
    LINIENBUS_IM_STADTVERKEHR(1, "Linienbus im Stadtverkehr"),
    METRO_UBAHN_SBAHN(3, "Metro/U-Bahn/S-Bahn"),
    STRASSENBAHN_TRAM(4, "Straßenbahn/TRAM"),
    SCHIFF(6, "Schiff"),
    ICE_HIGHSPEEDTRAIN(10, "ICE/highSpeedTrain"),
    UEBERLANDBUS_REGIONALBUS(11, "Überlandbus/Regionalbus"),
    REGIONALZUG(19, "Regionalzug"),
    IC(20, "IC"),
    STANDSEILBAHN(21, "Standseilbahn"),
    REGIONALEXPRESS(29, "Regionalexpress"),
    EXPRESSBUS(30, "Expressbus"),
    FLUGHAFENZUBRINGER(31, "Flughafenzubringer"),
    VERBUNDNAHVERKEHR(32, "Verbundnahverkehr"),
    VERBUNDNAHVERKEHR_OHNE_KURZSTRECKE(33, "Verbundnahverkehr ohne Kurzstrecke"),
    SPNV(34, "SPNV"),
    VERBUNDNAHVERKEHR_OHNE_SONDERVERKEHRSMITTEL(35, "Verbundnahverkehr ohne Sonderverkehrsmittel"),
    FAEHRE(36, "Fähre"),
    BERGBAHN(37, "Bergbahn"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_240(240, "frei für nicht interoperable Verwendung 240"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_241(241, "frei für nicht interoperable Verwendung 241"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_242(242, "frei für nicht interoperable Verwendung 242"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_243(243, "frei für nicht interoperable Verwendung 243"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_244(244, "frei für nicht interoperable Verwendung 244"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_245(245, "frei für nicht interoperable Verwendung 245"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_246(246, "frei für nicht interoperable Verwendung 246"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_247(247, "frei für nicht interoperable Verwendung 247"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_248(248, "frei für nicht interoperable Verwendung 248"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_249(249, "frei für nicht interoperable Verwendung 249"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_250(250, "frei für nicht interoperable Verwendung 250"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_251(251, "frei für nicht interoperable Verwendung 251"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_252(252, "frei für nicht interoperable Verwendung 252"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_253(253, "frei für nicht interoperable Verwendung 253"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_254(254, "frei für nicht interoperable Verwendung 254"),
    FREI_FUER_NICHT_INTEROPERABLE_VERWENDUNG_255(255, "frei für nicht interoperable Verwendung 255");

    final INT1 value;
    final String stringRepresentation;

    TransportmittelKategorieCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static TransportmittelKategorieCode of(final INT1 value) {
        for (final TransportmittelKategorieCode terminalTypCode : values()) {
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

    public static final ReadDescription<TransportmittelKategorieCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<TransportmittelKategorieCode> getReadDescription() {
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
