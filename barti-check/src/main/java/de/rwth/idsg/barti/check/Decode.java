package de.rwth.idsg.barti.check;

import de.rwth.idsg.barti.core.datatypes.composite.STB;
import de.rwth.idsg.barti.core.datatypes.pki.*;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Decode {

    static final HashMap<CHR, CVCert<?, ?, ?>> CHR_TO_CERTIFICATE = new HashMap<>();

    static {
        /*
         * ==============================================================================
         * Vorkonfiguration des Systems
         *
         * 1. Laden des Root-Schlüssels der VDV-KA-PKI
         * Der Schlüssel kann z.B. Bestandteil der Terminal-Software sein, so dass dieser
         * nach dem Laden der Software bereits vorhanden ist.
         *
         * 2. Laden der benötigten Sub-CA-Zertifikate der VDV-KA-PKI
         * 3. Ggf. Laden bekannter Signaturzertifikate
         *
         * ==============================================================================
         *
         * Prüfung einer statischen Berechtigung
         * 1. Einlesen einer statischen Berechtigung
         * 2. Signaturzertifikat holen und ggf. prüfen
         * Wurde das Signaturzertifikat erfolgreich verifiziert bzw. bereits gemäß Abschnitt 4.5.3 geladen, so wird
         * lediglich geprüft, ob es noch gültig ist (d.h. das End-of-Validity (EOV) noch nicht erreicht ist, siehe
         * (2) KA SAM-Spec, Abschnitt 4.3.1.5), und ob die Nutzung des Schlüssels für die Ausgabe von Berechtigungen
         * autorisiert ist (Prüfung, ob die CHA ein VDV-KA-SAM für Verkauf anzeigt, siehe (2) KA SAM-Spec,
         * Abschnitt 4.3.1.4).
         *
         * 3. Signatur prüfen
         * ISO/IEC 9796-2 Schema 1
         *
         * 4. Gültigkeit der Berechtigung prüfen
         * Die statischen Berechtigungsdaten gemäß Kap. 3, Tabelle 3-1 werden darauf hin geprüft, ob
         * - ein Sperreintrag für die Berechtigung in der Sperrliste vorhanden ist (gemäß VDV-KA bzw. Kap. 5),
         * - die Berechtigung räumlich und zeitlich gültig ist15,
         * - der Berechtigungshalter das ggf. in der Berechtigung referenzierte Kontrollmedium vorzeigt und
         * - der Berechtigungshalter im Falle einer personenbezogenen Berechtigung auch der Inhaber des in der
         * Berechtigung referenzierten personenbezogenen Kontrollmediums ist.
         * 5. Kontrollnachweis schreiben
         *
         */
        try {
            final SamCHR chr = SamCHR.READ_DESCRIPTION.read(new byte[]{
                    (byte) 0x88, (byte) 0xF3, (byte) 0x18, (byte) 0x10, (byte) 0x13, (byte) 0x10,
                    (byte) 0x31, (byte) 0x88, (byte) 0xF3, (byte) 0x09, (byte) 0xFB, (byte) 0xF1
            });
            final SamCVCert cvCert = SamCVCert.READ_DESCRIPTION.read(new byte[]{
                    (byte) 0x7F, (byte) 0x21, (byte) 0x82, (byte) 0x01, (byte) 0x75, (byte) 0x5F, (byte) 0x4E,
                    (byte) 0x81, (byte) 0xAD, (byte) 0x06, (byte) 0x44, (byte) 0x45, (byte) 0x56, (byte) 0x44,
                    (byte) 0x56, (byte) 0x13, (byte) 0x01, (byte) 0x14, (byte) 0x88, (byte) 0xF3, (byte) 0x18,
                    (byte) 0x10, (byte) 0x13, (byte) 0x10, (byte) 0x31, (byte) 0x88, (byte) 0xF3, (byte) 0x09,
                    (byte) 0xFB, (byte) 0xF1, (byte) 0x56, (byte) 0x44, (byte) 0x56, (byte) 0x5F, (byte) 0x4B,
                    (byte) 0x41, (byte) 0x22, (byte) 0x20, (byte) 0x18, (byte) 0x10, (byte) 0x31, (byte) 0x2A,
                    (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xF7, (byte) 0x0D, (byte) 0x01, (byte) 0x01,
                    (byte) 0x0A, (byte) 0x91, (byte) 0x32, (byte) 0xAA, (byte) 0xAD, (byte) 0xD7, (byte) 0xA9,
                    (byte) 0x99, (byte) 0x33, (byte) 0x77, (byte) 0xB9, (byte) 0x6E, (byte) 0x93, (byte) 0x5C,
                    (byte) 0x53, (byte) 0x03, (byte) 0xD8, (byte) 0xEB, (byte) 0x7D, (byte) 0x38, (byte) 0x93,
                    (byte) 0xE4, (byte) 0xE2, (byte) 0xE4, (byte) 0xFB, (byte) 0x5B, (byte) 0x36, (byte) 0xBE,
                    (byte) 0x32, (byte) 0x6C, (byte) 0x3B, (byte) 0x45, (byte) 0x31, (byte) 0x41, (byte) 0xC5,
                    (byte) 0xA9, (byte) 0x5E, (byte) 0xC6, (byte) 0x05, (byte) 0xE1, (byte) 0x95, (byte) 0x7D,
                    (byte) 0xED, (byte) 0x7F, (byte) 0x0B, (byte) 0x79, (byte) 0xA6, (byte) 0xF5, (byte) 0xF6,
                    (byte) 0xC6, (byte) 0x86, (byte) 0xDD, (byte) 0x67, (byte) 0xBA, (byte) 0xBF, (byte) 0xCC,
                    (byte) 0x93, (byte) 0xDA, (byte) 0x3C, (byte) 0xE9, (byte) 0xBD, (byte) 0x6A, (byte) 0x73,
                    (byte) 0x5B, (byte) 0x5E, (byte) 0xEE, (byte) 0x2C, (byte) 0x88, (byte) 0xAA, (byte) 0xB5,
                    (byte) 0x15, (byte) 0x2D, (byte) 0x62, (byte) 0x13, (byte) 0x53, (byte) 0xFC, (byte) 0xE6,
                    (byte) 0xC0, (byte) 0x5B, (byte) 0xA8, (byte) 0xBD, (byte) 0xCB, (byte) 0xD0, (byte) 0x0D,
                    (byte) 0xD8, (byte) 0xB9, (byte) 0x86, (byte) 0x68, (byte) 0x00, (byte) 0x97, (byte) 0xF5,
                    (byte) 0x05, (byte) 0x2E, (byte) 0x6A, (byte) 0x84, (byte) 0xDE, (byte) 0xB4, (byte) 0x26,
                    (byte) 0x5E, (byte) 0x0B, (byte) 0x02, (byte) 0xDD, (byte) 0x12, (byte) 0x8B, (byte) 0xCC,
                    (byte) 0x5E, (byte) 0xEF, (byte) 0x17, (byte) 0x6D, (byte) 0xB1, (byte) 0x6D, (byte) 0x31,
                    (byte) 0xDB, (byte) 0x20, (byte) 0x2D, (byte) 0x70, (byte) 0xC2, (byte) 0xE8, (byte) 0x82,
                    (byte) 0xF6, (byte) 0x03, (byte) 0x78, (byte) 0x71, (byte) 0xA7, (byte) 0xC0, (byte) 0xE3,
                    (byte) 0x53, (byte) 0x0F, (byte) 0x5F, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                    (byte) 0x5F, (byte) 0x37, (byte) 0x81, (byte) 0xC0, (byte) 0x01, (byte) 0xC5, (byte) 0x23,
                    (byte) 0x4D, (byte) 0xF6, (byte) 0x12, (byte) 0xE2, (byte) 0xC9, (byte) 0xE2, (byte) 0x9E,
                    (byte) 0xC0, (byte) 0x98, (byte) 0xF7, (byte) 0x3D, (byte) 0x32, (byte) 0x7A, (byte) 0x6F,
                    (byte) 0x23, (byte) 0xDD, (byte) 0xED, (byte) 0x71, (byte) 0x7B, (byte) 0x19, (byte) 0x61,
                    (byte) 0x9B, (byte) 0x33, (byte) 0xBA, (byte) 0x18, (byte) 0x2C, (byte) 0x85, (byte) 0x72,
                    (byte) 0x2E, (byte) 0xDB, (byte) 0x71, (byte) 0xD5, (byte) 0xC2, (byte) 0xCB, (byte) 0x5C,
                    (byte) 0x40, (byte) 0x80, (byte) 0x0B, (byte) 0xE1, (byte) 0x85, (byte) 0xB5, (byte) 0x9B,
                    (byte) 0x16, (byte) 0x66, (byte) 0x82, (byte) 0xA6, (byte) 0xA4, (byte) 0x32, (byte) 0xAA,
                    (byte) 0x89, (byte) 0x8B, (byte) 0xE8, (byte) 0x1D, (byte) 0xCA, (byte) 0xF5, (byte) 0xC6,
                    (byte) 0x17, (byte) 0x3A, (byte) 0x86, (byte) 0x06, (byte) 0x44, (byte) 0x07, (byte) 0x94,
                    (byte) 0xB2, (byte) 0x18, (byte) 0xDB, (byte) 0x69, (byte) 0x70, (byte) 0x17, (byte) 0x8D,
                    (byte) 0x06, (byte) 0x84, (byte) 0xFF, (byte) 0x68, (byte) 0x33, (byte) 0x54, (byte) 0x04,
                    (byte) 0xF1, (byte) 0xF9, (byte) 0xA7, (byte) 0xBB, (byte) 0xA5, (byte) 0x22, (byte) 0x53,
                    (byte) 0x6D, (byte) 0xA2, (byte) 0xB5, (byte) 0x90, (byte) 0x8A, (byte) 0x92, (byte) 0x1C,
                    (byte) 0xC9, (byte) 0xF5, (byte) 0x6C, (byte) 0x13, (byte) 0x0A, (byte) 0x57, (byte) 0x2F,
                    (byte) 0xA8, (byte) 0xCE, (byte) 0xAB, (byte) 0xB8, (byte) 0xEB, (byte) 0x03, (byte) 0xA7,
                    (byte) 0xD5, (byte) 0x9C, (byte) 0x2F, (byte) 0x94, (byte) 0x52, (byte) 0x8B, (byte) 0x64,
                    (byte) 0xDD, (byte) 0x2A, (byte) 0x68, (byte) 0x33, (byte) 0x47, (byte) 0x52, (byte) 0x40,
                    (byte) 0x4D, (byte) 0xE2, (byte) 0x30, (byte) 0x0B, (byte) 0x7B, (byte) 0xF5, (byte) 0x84,
                    (byte) 0x66, (byte) 0xA4, (byte) 0x8E, (byte) 0x17, (byte) 0x0B, (byte) 0x34, (byte) 0x63,
                    (byte) 0xE8, (byte) 0x94, (byte) 0xB5, (byte) 0x85, (byte) 0xB5, (byte) 0xBA, (byte) 0xE1,
                    (byte) 0xFD, (byte) 0xFB, (byte) 0xB4, (byte) 0x55, (byte) 0x46, (byte) 0x73, (byte) 0x5F,
                    (byte) 0x3C, (byte) 0x81, (byte) 0xFA, (byte) 0x7B, (byte) 0xD6, (byte) 0x05, (byte) 0x20,
                    (byte) 0x28, (byte) 0xE9, (byte) 0x7C, (byte) 0x55, (byte) 0xD6, (byte) 0x88, (byte) 0xA0,
                    (byte) 0x5B, (byte) 0x9E, (byte) 0x30, (byte) 0x4B, (byte) 0xB2, (byte) 0x2B, (byte) 0x39,
                    (byte) 0x61, (byte) 0x42, (byte) 0x95, (byte) 0x8F, (byte) 0x5E, (byte) 0x24, (byte) 0x07,
                    (byte) 0x61, (byte) 0x46, (byte) 0xDE, (byte) 0xC8, (byte) 0x87, (byte) 0xB3, (byte) 0x70,
                    (byte) 0xF2, (byte) 0x12, (byte) 0x42, (byte) 0xE3, (byte) 0x2F, (byte) 0xF0, (byte) 0x01});
            CHR_TO_CERTIFICATE.put(chr, cvCert);
        } catch (final IOException e) {
            throw new Error(e);
        }
    }

    private static byte[] checkSignatureAndGetRecoverableText(final byte[] signedSTBWithCHR)
            throws InvalidTicketException {
        if (signedSTBWithCHR.length < 138) {
            throw new InvalidTicketException("Too few bytes to be a valid signed ticket!");
        }
        final byte[] actualSignature = Arrays.copyOfRange(signedSTBWithCHR, 3, 131);
        final byte restLength = signedSTBWithCHR[132];
        final int restOffset = 133;
        final int chrStart = restOffset + restLength + 3;
        final SamCHR chr;
        try {
            chr = SamCHR.READ_DESCRIPTION.read(Arrays.copyOfRange(signedSTBWithCHR, chrStart, chrStart + 12));
        } catch (final IOException e) {
            throw new InvalidTicketException("No Certificate Holder Reference identifiable!", e);
        }
        final SamCVCert certificate = (SamCVCert) CHR_TO_CERTIFICATE.get(chr);
        final ISO9796d2Signer iso9796d2Signer = new ISO9796d2Signer(new RSAEngine(), new SHA1Digest(), true);
        iso9796d2Signer.init(false, getRSAPublicKeyParameters(certificate.getKey()));
        final byte[] recoveredMessage;
        try {
            iso9796d2Signer.updateWithRecoveredMessage(actualSignature);
            iso9796d2Signer.update(signedSTBWithCHR, restOffset, restLength);
            if (!iso9796d2Signer.verifySignature(actualSignature)) {
                throw new InvalidTicketException("Signature tampered with!");
            }
            recoveredMessage = iso9796d2Signer.getRecoveredMessage();
        } catch (final InvalidCipherTextException e) {
            throw new InvalidTicketException("Cipher text does not match expected schema!", e);
        }
        final byte[] stb = new byte[recoveredMessage.length + restLength];
        System.arraycopy(recoveredMessage, 0, stb, 0, recoveredMessage.length);
        System.arraycopy(signedSTBWithCHR, restOffset, stb, 106, restLength);
        return stb;
    }

    public static STB decodeAndCheck(final byte[] rawBytes) throws InvalidTicketException {
        try {
            return STB.READ_DESCRIPTION.read(checkSignatureAndGetRecoverableText(rawBytes));
        } catch (final IOException e) {
            throw new InvalidTicketException("Correct Signature, yet incorrect ticket format!", e);
        }
    }

    public static RSAKeyParameters getRSAPublicKeyParameters(final CVKey<?, ?> key) {
        return new RSAKeyParameters(false, toBigInt(key.getModulus()), toBigInt(key.getExponent()));
    }

    public static BigInteger toBigInt(final byte[] arrayValue) {
        return new BigInteger(1, arrayValue);
    }
}
