package de.rwth.idsg.barti.sam.communication;

import de.intarsys.security.smartcard.card.ICardConnection;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.pki.BetreiberCHR;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import de.rwth.idsg.barti.core.datatypes.pki.SamCVKey;
import de.rwth.idsg.barti.sam.EfSamID;
import lombok.Value;

import java.util.List;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class SamInfo {
    ICardConnection connection;
    EfSamID efSamID;
    BetreiberCHR betreiberCHR;
    List<KeyInfo> pvMKs;
    SamCVKey samSigKey;

    public KeyInfo getKeyForPv(final ReferenceNumberTwo pvOrgId) {
        for (KeyInfo keyInfo : pvMKs) {
            if (keyInfo.getOrgID().equals(pvOrgId)) {
                return keyInfo;
            }
        }
        throw new IllegalArgumentException("samInfo doesn't contain a MK for PV " + pvOrgId.toString() + "!");
    }
}
