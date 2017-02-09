package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-43
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum BezahlArtCode implements Data<BezahlArtCode> {
    KEINE_ANGABE(0, "keine Angabe"),
    BAR(1, "bar"),
    KREDITKARTE(3, "Kreditkarte"),
    POBPEB(5, "POB/PEB"),
    ECKARTELASTSCHRIFT(6, "ec-Karte/Lastschrift"),
    RECHNUNG(7, "Rechnung"),
    WERTEINHEITEN(8, "Werteinheiten"),
    GUTSCHEIN_VOUCHER(14, "Gutschein/Voucher"),
    EC_CASH(17, "ec cash"),
    RFU_CEN_19(19, "RFU CEN 19"),
    RFU_CEN_20(20, "RFU CEN 20"),
    RFU_CEN_21(21, "RFU CEN 21"),
    RFU_CEN_22(22, "RFU CEN 22"),
    RFU_CEN_23(23, "RFU CEN 23"),
    GELD_KARTE(24, "GeldKarte"),
    MASTERCARD(25, "Mastercard"),
    VISACARD(26, "Visacard"),
    HANDY_TICKET_KONTO(27, "HandyTicket Konto"),
    MOBILFUNKRECHNUNG(28, "Mobilfunkrechnung");


    final INT1 value;
    final String stringRepresentation;

    BezahlArtCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static BezahlArtCode of(final INT1 value) {
        for (final BezahlArtCode terminalTypCode : values()) {
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

    public static final ReadDescription<BezahlArtCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<BezahlArtCode> getReadDescription() {
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
