package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.CardException;
import de.intarsys.security.smartcard.card.ICardConnection;
import de.intarsys.security.smartcard.card.RequestAPDU;
import de.intarsys.security.smartcard.card.ResponseAPDU;
import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVCert;
import de.rwth.idsg.barti.sam.EfSamID;
import de.rwth.idsg.barti.sam.exception.SamException;
import de.rwth.idsg.barti.sam.exception.SamFileNotFoundException;
import de.rwth.idsg.barti.sam.exception.WrappedIOException;
import java8.util.stream.IntStreams;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.rwth.idsg.barti.sam.communication.Common.transmit;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Log4j2
public class ReadRecord {

    public static SamCVCert readCertAuth(final ICardConnection connection) throws CardException, SamException {
        // execute READ RECORD(EF_CERT_AUTH)
        // 4.3.3.1.1 CV-Zertifikat des SAM-Authentisierungsschlüssels mit Signatur gemäß PKCS#1_v1.5 (Signatur im
        // Anhang)
        return readCert(connection, 2, SPI.EF_CERT_AUTH, 0x0178);
    }

    public static SamCVCert readCertSig(final ICardConnection connection) throws CardException, SamException {
        // execute READ RECORD(EF_CERT_SIG)
        // 4.3.3.1.4 CV-Zertifikat des SAM-Signaturschlüssels mit Signatur gemäß PKCS#1_v1.5 (Signatur im Anhang)
        return readCert(connection, 1, SPI.EF_CERT_SIG, 0x017A);
    }

    public static byte[] readBetreiberCHRRaw(final ICardConnection connection) throws CardException, SamException {
        // execute READ RECORD(EF_SAM-Betreiber-ID)
        // 4.3.1.3.4 CHR für Zertifikate zur Aktivierung des SAM
        return readRecord(connection, 1, SPI.EF_SAM_BETREIBER_ID, 0xC);
    }

    public static BetreiberCHR readBetreiberCHR(final ICardConnection connection) throws CardException, SamException {
        try {
            return BetreiberCHR.READ_DESCRIPTION.read(readBetreiberCHRRaw(connection));
        } catch (final IOException e) {
            throw new WrappedIOException(e);
        }
    }

    public static EfSamID readSamID(final ICardConnection connection) throws CardException, SamException {
        final byte[] data = readRecord(connection, 1, SPI.EF_SAM_ID, 0x0C);
        return EfSamID.fromBytes(data);
    }

    private static SamCVCert readCert(final ICardConnection connection, final int recordNumber, final SPI spi, final int
            returnLength) throws CardException, SamException {
        final byte[] bytes = readRecord(connection, recordNumber, spi, returnLength);
        try {
            return SamCVCert.READ_DESCRIPTION.read(bytes);
        } catch (final IOException e) {
            throw new WrappedIOException(e);
        }
    }

    private static final short UNUSED_KEY_ID = 0x00;
    private static final short INVALIDATED_KEY_ID = 0x80;
    private static final short NULL_ENTRY_VERSION = 0x00;
    private static final int[] PVMK_RECORDS = IntStreams.concat(
            IntStreams.of(13, 14),
            IntStreams.rangeClosed(41, 78)
    ).toArray();

    public static List<KeyInfo> readPVMKs(final ICardConnection connection) throws CardException, SamException {
        final List<KeyInfo> keys = new ArrayList<>();
        // search through all positions relevant for PV MKs in EF_Schluessel-Info
        log.debug("Searching for PV MKs in EF_Schluessel-Info");
        for (final int index : PVMK_RECORDS) {
            final byte[] bytes = readRecord(connection, index, SPI.EF_SCHLUESSEL_INFO, 12);
            try {
                final KeyInfo keyInfo = KeyInfo.READ_DESCRIPTION.read(bytes);
                assert (short) 0x40 == keyInfo.getKeyID().getValue().getValue()
                        : "Only PV MKs should be contained here!";
                if (NULL_ENTRY_VERSION == keyInfo.getKeyVersion().getValue().getValue()) {
                    continue;
                }
                if (0L == keyInfo.getUsageLimit().getValue().getValue()) {
                    // key can't be used anymore
                    continue;
                }
                keys.add(keyInfo);
            } catch (final IOException e) {
                throw new WrappedIOException(e);
            }
        }
        // search through EF_Schlüssel-Info-Erweiterung-PV until an unused slot is found
        log.debug("Searching for PV MKs in EF_Schluessel-Info-Erweiterung-PV");
        int record = 1;
        // the record numbers 0 and 255 may not be used
        try {
            while (record < 255) {
                final byte[] bytes = readRecord(connection, record, SPI.EF_SCHLUESSEL_INFO_ERWEITERUNG_PV, 12);
                final KeyInfo keyInfo = KeyInfo.READ_DESCRIPTION.read(bytes);
                final short keyID = keyInfo.getKeyID().getValue().getValue();
                if (UNUSED_KEY_ID == keyID) {
                    break;
                }
                if (INVALIDATED_KEY_ID == keyID) {
                    continue;
                }
                if (0L == keyInfo.getUsageLimit().getValue().getValue()) {
                    // key can't be used anymore
                    continue;
                }
                assert (short) 0x40 == keyID : "Only PV MKs should be contained here!";
                keys.add(keyInfo);
                ++record;
            }
        } catch (final SamFileNotFoundException e) {
            // since we get a 0x6A82 response when trying to access empty EF_Schluessel-Info-Erweiterung-PV, ignore it
        } catch (final IOException e) {
            throw new WrappedIOException(e);
        }
        return keys;
    }

    private static byte[] readRecord(final ICardConnection connection, final int recordNumber, final SPI spi,
                                     final int returnLength) throws CardException, SamException {
        final ResponseAPDU response = transmit(connection, new RequestAPDU(0x00, 0xb2, recordNumber,
                (spi.getValue() << 3) | 0x4, returnLength));
        final int sw = response.getSw();
        switch (sw) {
            case 0x9000:
                // SUCCESS
                break;
            case 0x6281:
                throw new SamException("READ RECORD: Teile der zurückgegebenen Daten können fehlerhaft sein.", sw);
            case 0x6282:
                throw new SamException("READ RECORD: Ende des Records erreicht, bevor Le Bytes gelesen wurden "
                        + "(nur bei Le ungleich Null).", sw);
            case 0x6581:
                throw new SamException("READ RECORD: Memory failure", sw);
            case 0x6700:
                throw new SamException("READ RECORD: Wrong length", sw);
            case 0x6982:
                throw new SamException("READ RECORD: Security status not satisfied", sw);
            case 0x6a82:
                throw new SamFileNotFoundException("READ RECORD: File not found");
            case 0x6a83:
                throw new SamException("READ RECORD: Record not found", sw);
            case 0x6a86:
                throw new SamException("READ RECORD: Incorrect parameters P1-P2", sw);
            case 0x6e00:
                throw new SamException("READ RECORD: CLA (for INS) not supported", sw);
            default:
                throw new SamException("READ RECORD: "
                        + Integer.toHexString(sw), sw);
        }
        return response.getData();
    }
}
