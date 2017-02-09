package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberOne;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.basic.SequenceNumberFour;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class KeyInfo implements Data<KeyInfo> {
    final ReferenceNumberOne keyID;
    final ReferenceNumberOne keyVersion;
    final ReferenceNumberTwo orgID;
    final SequenceNumberFour usageLimit;
    final SequenceNumberFour usageSequenceCount;

    public static final ReadDescription<KeyInfo> READ_DESCRIPTION = stream -> {
        final ReferenceNumberOne keyID = ReferenceNumberOne.READ_DESCRIPTION.read(stream);
        final ReferenceNumberOne keyVersion = ReferenceNumberOne.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo orgID = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        final SequenceNumberFour usageLimit = SequenceNumberFour.READ_DESCRIPTION.read(stream);
        final SequenceNumberFour usageSequenceCount = SequenceNumberFour.READ_DESCRIPTION.read(stream);
        return new KeyInfo(keyID, keyVersion, orgID, usageLimit, usageSequenceCount);
    };

    @Override public ReadDescription<KeyInfo> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        keyID.write(stream);
        keyVersion.write(stream);
        orgID.write(stream);
        usageLimit.write(stream);
        usageSequenceCount.write(stream);
    }

    @Override
    public int getLength() {
        return 12;
    }

    @Override
    public String toString() {
        return "KeyInfo: ["
                + "ID: " + keyID + ", "
                + "version: " + keyVersion + ", "
                + "orgID: " + orgID + ", "
                + "usageLimit: " + usageLimit + ", "
                + "usageSequenceCount: " + usageSequenceCount + ']';
    }
}
