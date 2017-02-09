package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT2;
import de.rwth.idsg.barti.core.datatypes.basic.PrintableStringEight;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
@RequiredArgsConstructor
public class BetreiberCHR implements CHR<BetreiberCHR> {
    final INT2 orgID;
    final PrintableStringEight activate;
    final INT2 keyGeneration;

    public static final ReadDescription<BetreiberCHR> READ_DESCRIPTION = stream -> {
        final INT2 orgID = INT2.READ_DESCRIPTION.read(stream);
        final PrintableStringEight activate = PrintableStringEight.READ_DESCRIPTION.read(stream);
        final INT2 keyGeneration = INT2.READ_DESCRIPTION.read(stream);
        return new BetreiberCHR(orgID, activate, keyGeneration);
    };

    @Override public ReadDescription<BetreiberCHR> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        orgID.write(stream);
        activate.write(stream);
        keyGeneration.write(stream);
    }
}
