package de.rwth.idsg.barti.core.datatypes.enums;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 6-70
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public enum TerminalTypCode implements Data<TerminalTypCode> {
    UNBESTIMMT(0, "Unbestimmt"),
    ERFASSUNGSTERMINAL_CICO_BIBO(1, "Erfassungsterminal für CICO/BIBO"),
    VERKAUFSAUTOMAT(2, "Verkaufsautomat (z. B. Berechtigungen)"),
    KONTROLLTERMINAL_MOBIL_PERSONALBEDIENT(3, "Kontrollterminal (mobil, personalbedient)"),
    KARTENAUSGABETERMINAL(4, "Kartenausgabeterminal"),
    KARTENRUECKGABETERMINAL(5, "Kartenrückgabeterminal"),
    EINSTIEGSKONTROLLGERAET_ENTWERTER(6, "Einstiegskontrollgerät/Entwerter"),
    MULTIFUNKTIONSTERMINAL_KUNDENBEDIENT(7, "Multifunktionsterminal (kundenbedient)"),
    INFORMATIONSTERMINAL(8, "Informationsterminal"),
    LADETERMINAL_OEPV_WERTEINHEITEN(9, "Ladeterminal für ÖPV-Werteinheiten"),
    TERMINAL_BEIM_MASSENPERSONALISIERER(13, "Terminal beim Massenpersonalisierer"),
    TERMINAL_IN_VERTRIEBS_ODER_SERVICEHALTESTELLE(14, "Terminal in Vertriebs- oder Servicestellestelle "
            + "(personalbedient)"),
    FAHRERTERMINAL_VERKAUF_UND_KONTROLLE(15, "Fahrerterminal (Verkauf und Kontrolle)"),
    HANDY_TICKETSERVER(16, "HandyTicketserver"),
    EONLINE_TICKETSERVER(17, "eOnline Ticketserver"),
    VERKAUFSAUTOMAT_MOBIL_KUNDENBEDIENT(18, "Verkaufsautomat mobil (kundenbedient)"),
    VERKAUFS_UND_KONTROLLTERMINAL_MOBIL_PERSONALBEDIENT(19, "Verkaufs- und Kontrollterminalmobil (personalbedient)");

    final INT1 value;
    final String stringRepresentation;

    TerminalTypCode(final int value, final String stringRepresentation) {
        this(new INT1((short) value), stringRepresentation);
    }

    public static TerminalTypCode of(final INT1 value) {
        for (final TerminalTypCode terminalTypCode : values()) {
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

    public static final ReadDescription<TerminalTypCode> READ_DESCRIPTION = stream -> of(INT1
            .READ_DESCRIPTION.read(stream));

    @Override
    public ReadDescription<TerminalTypCode> getReadDescription() {
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
