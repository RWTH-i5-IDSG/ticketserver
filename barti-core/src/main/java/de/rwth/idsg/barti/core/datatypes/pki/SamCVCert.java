package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SamCVCert extends CVCert<SamCHR, SamCVKey, SamCVCert> {

    public SamCVCert(final MyInputStream stream) throws IOException {
        super(stream, SamCVKey.READ_DESCRIPTION);
    }

    public static final ReadDescription<SamCVCert> READ_DESCRIPTION = SamCVCert::new;

    @Override
    public ReadDescription<SamCVCert> getReadDescription() {
        return READ_DESCRIPTION;
    }
}
