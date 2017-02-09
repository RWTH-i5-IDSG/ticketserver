package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class BetreiberCVKey extends CVKey<BetreiberCHR, BetreiberCVKey> {
    public BetreiberCVKey(final MyInputStream stream) throws IOException {
        super(stream, BetreiberCHR.READ_DESCRIPTION);
    }

    public static final ReadDescription<BetreiberCVKey> READ_DESCRIPTION = BetreiberCVKey::new;

    @Override
    public ReadDescription<BetreiberCVKey> getReadDescription() {
        return READ_DESCRIPTION;
    }
}
