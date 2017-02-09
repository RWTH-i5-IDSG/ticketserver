package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.sam.exception.SamFileNotFoundException;

import static de.rwth.idsg.barti.sam.communication.Common.transmit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SelectApplication {
    public static final byte[] DF_VDV_SAM_AID = new byte[]{
            (byte) 0xD2, (byte) 0x76, (byte) 0x00, (byte) 0x01, (byte) 0x35, (byte) 0x4B,
            (byte) 0x41, (byte) 0x53, (byte) 0x4D, (byte) 0x30, (byte) 0x31, (byte) 0x00
    };
    public static final RequestAPDU SELECT_FILE_DF_VDV_SAM = new RequestAPDU(0x00, 0xA4, 0x04, 0x00,
            DF_VDV_SAM_AID, 0x1C);

    public static Void selectApplication(final ICardConnection connection) throws CardException, SamException {
        // execute SELECT FILE
        final ResponseAPDU response = transmit(connection, SELECT_FILE_DF_VDV_SAM);
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("SELECT FILE: Execution error, state of non-volatile memory unchanged", sw);
            case 0x6581:
                throw new SamException("SELECT FILE: Memory failure", sw);
            case 0x6700:
                throw new SamException("SELECT FILE: Wrong length", sw);
            case 0x6a82:
                throw new SamFileNotFoundException("SELECT FILE: File not found");
            case 0x6a86:
                throw new SamException("SELECT FILE: Incorrect parameters P1-P2", sw);
            case 0x6a87:
                throw new SamException("SELECT FILE: Number of Bytes in the command data field inconsistent with the "
                        + "parameters P1-P2", sw);
            case 0x6e00:
                throw new SamException("SELECT FILE: CLA (for INS) not supported", sw);
            default:
                throw new SamException("SELECT FILE: "
                        + Integer.toHexString(sw), sw);
        }
        return null;
    }
}
