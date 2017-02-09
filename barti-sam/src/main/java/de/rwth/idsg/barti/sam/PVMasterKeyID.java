package de.rwth.idsg.barti.sam;

import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberOne;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.pki.KeyInfo;
import lombok.Value;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class PVMasterKeyID {
    final ReferenceNumberOne keyVersion;
    final ReferenceNumberTwo orgID;

    public static PVMasterKeyID fromKeyInfo(final KeyInfo keyInfo) {
        if (keyInfo.getKeyID().getValue().getValue() != 0x40) {
            throw new IllegalArgumentException();
        }
        return new PVMasterKeyID(keyInfo.getKeyVersion(), keyInfo.getOrgID());
    }
}
