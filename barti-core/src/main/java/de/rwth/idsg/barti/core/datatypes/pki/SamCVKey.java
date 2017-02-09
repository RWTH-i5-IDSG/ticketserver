package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamCVKey extends CVKey<SamCHR, SamCVKey> {
    public SamCVKey(final MyInputStream stream) throws IOException {
        super(stream, SamCHR.READ_DESCRIPTION);
    }

    public static final ReadDescription<SamCVKey> READ_DESCRIPTION = SamCVKey::new;

    @Override
    public ReadDescription<SamCVKey> getReadDescription() {
        return READ_DESCRIPTION;
    }
}
