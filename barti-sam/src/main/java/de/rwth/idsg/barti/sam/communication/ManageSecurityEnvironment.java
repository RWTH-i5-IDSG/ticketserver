package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.sam.exception.SamException;

import java.nio.ByteBuffer;

import static de.rwth.idsg.barti.sam.communication.Common.transmit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ManageSecurityEnvironment {
    public static void manageSecurityEnvironment(final ICardConnection connection, final byte[] userKeyCHR)
            throws CardException, SamException {
        final ByteBuffer commandData = ByteBuffer.allocate(14);
        // tag
        commandData.put((byte) 0x83);
        // length
        commandData.put((byte) 0x0C);
        commandData.put(userKeyCHR);
        final ResponseAPDU response = transmit(connection, new RequestAPDU((byte) 0x00, (byte) 0x22, (byte) 0xC1,
                (byte) 0xA4, commandData.array()));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: Execution Error, State of non-volatile "
                        + "memory unchanged", sw);
            case 0x6581:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: Memory failure", sw);
            case 0x6700:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: Wrong length", sw);
            case 0x6a80:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: Incorrect parameters in the data field", sw);
            case 0x6a86:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: Incorrect parameters P1-P2", sw);
            case 0x6e00:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: CLA (for INS) not supported", sw);
            default:
                throw new SamException("MANAGE SECURITY ENVIRONMENT: "
                        + Integer.toHexString(sw), sw);
        }
    }
}
