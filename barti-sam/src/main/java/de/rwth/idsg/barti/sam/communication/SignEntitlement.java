package de.rwth.idsg.barti.sam.communication;

import com.google.common.primitives.UnsignedBytes;
import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.core.aggregate.ProductConfiguration;
import de.rwth.idsg.barti.core.aggregate.STBParameters;
import de.rwth.idsg.barti.core.datatypes.composite.STB;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.sam.STBCreator;
import de.rwth.idsg.barti.sam.exception.ConditionsOfUseNotSatisfiedException;
import de.rwth.idsg.barti.sam.exception.SamException;

import java.nio.ByteBuffer;

import static de.rwth.idsg.barti.sam.communication.Common.transmit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SignEntitlement {
    public static ByteBuffer signEntitlement(final ICardConnection connection,
                                             final KeyInfo pvMK,
                                             final byte[] samSigKeyCHR,
                                             final long ticketSequenceNumber,
                                             final STBParameters parameters,
                                             final ProductConfiguration productConfiguration)
            throws CardException, SamException {
        final STB stb = STBCreator.createSTB(ticketSequenceNumber, parameters, productConfiguration);
        final byte[] stbByteArray = stb.write();
        final int offsetBerProdLogSAMSeqNummer = stb.getInnerLength() - 12;
        final int offsetPVOrgID = 8;
        // execute SIGN ENTITLEMENT
        return signEntitlement(connection, stbByteArray, offsetBerProdLogSAMSeqNummer,
                offsetPVOrgID, pvMK.getKeyVersion().write(), pvMK.getOrgID().write(), samSigKeyCHR);
    }

    private static ByteBuffer signEntitlement(final ICardConnection connection, final byte[] stbByteArray,
                                              final int offsetBerProdLogSAMSeqNummer, final int offsetPVOrgID,
                                              final byte[] pvKeyVersion, final byte[] pvOrgID,
                                              final byte[] samSigKeyCHR) throws CardException, SamException {
        final ResponseAPDU response = transmit(connection, new RequestAPDU(
                0x80, 0x50, offsetPVOrgID, offsetBerProdLogSAMSeqNummer,
                ByteBuffer.allocate(stbByteArray.length + 9)
                        // PARAMETER EINS
                        // Schlüsselreferenz-Datenobject (PV-Masterkey)
                        .put((byte) 0xC4).put((byte) 0x04)
                        // Key Identifier
                        .put((byte) 0x40)
                        // Key Version
                        .put(pvKeyVersion)
                        // PV Org.-ID
                        .put(pvOrgID)
                        // PARAMETER ZWEI
                        // Inputdaten für Signatur
                        .put((byte) 0x9a).put((byte) 0x81)
                        .put((byte) stbByteArray.length)
                        .put(stbByteArray)
                        .array(),
                // FIXME allow more than 255 return bytes
                0xFF));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6400:
                throw new SamException("SIGN ENTITLEMENT: Execution error, state of non-volatile memory "
                        + "unchanged", sw);
            case 0x6581:
                throw new SamException("SIGN ENTITLEMENT: Memory failure", sw);
            case 0x6700:
                throw new SamException("SIGN ENTITLEMENT: Wrong length", sw);
            case 0x6882:
                throw new SamException("SIGN ENTITLEMENT: Secure messaging not supported", sw);
            case 0x6985:
                throw new ConditionsOfUseNotSatisfiedException("SIGN ENTITLEMENT: Conditions of use not satisfied");
            case 0x6a80:
                throw new SamException("SIGN ENTITLEMENT: Incorrect parameters in the data field", sw);
            case 0x6a88:
                throw new SamException("SIGN ENTITLEMENT: Referenced data not found", sw);
            case 0x6e00:
                throw new SamException("SIGN ENTITLEMENT: CLA (for INS) not supported", sw);
            default:
                throw new SamException("SIGN ENTITLEMENT: " + Integer.toHexString(sw), sw);
        }
        final byte[] signature = response.getData();
        if (signature[0] != (byte) 0x9E) throw new IllegalStateException();
        final boolean includeSum = signature.length + 15 < 153;
        final int fullLength = signature.length + (includeSum ? 15 : 16);
        final ByteBuffer result = ByteBuffer.allocate(fullLength);
        result.put(signature);
        // Tag für CHR
        result.put((byte) 0x5F).put((byte) 0x20);
        // Länge CHR
        result.put((byte) 0x0C);
        // CHR
        result.put(samSigKeyCHR);
        if (includeSum) {
            result.put(UnsignedBytes.checkedCast(fullLength));
        }
        return result;
    }
}
