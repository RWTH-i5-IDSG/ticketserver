package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class CACVKey extends CVKey<CACHR, CACVKey> {
    public CACVKey(final MyInputStream stream) throws IOException {
        super(stream, CACHR.READ_DESCRIPTION);
    }

    public static final ReadDescription<CACVKey> READ_DESCRIPTION = CACVKey::new;

    @Override
    public ReadDescription<CACVKey> getReadDescription() {
        return READ_DESCRIPTION;
    }
}
