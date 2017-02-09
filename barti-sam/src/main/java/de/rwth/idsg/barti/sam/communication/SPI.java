package de.rwth.idsg.barti.sam.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@AllArgsConstructor
public enum SPI {
    EF_SAM_ID((byte) 0x01), EF_SAM_BETREIBER_ID((byte) 0x02), EF_FCI((byte) 0x03), EF_TRANSAKTIONSZAEHLER((byte)
            0x04), EF_LOAD_KEY_ZAEHLER((byte) 0x05), EF_SCHLUESSEL_INFO((byte) 0x06), EF_CERT_AUTH((byte) 0x07),
    EF_CERT_SIG((byte) 0x08), EF_CERT_ENC((byte) 0x09), EF_AKTIVIERUNGSSTATUS((byte) 0x10),
    EF_SCHLUESSEL_INFO_ERWEITERUNG_KVP((byte) 0x11), EF_SCHLUESSEL_INFO_ERWEITERUNG_PV((byte) 0x12);
    final byte value;
}
