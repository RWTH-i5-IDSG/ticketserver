package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT1;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.enums.ProfilCode;

import java.io.IOException;

/**
 * 5-13
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class Mitnahme implements Data<Mitnahme> {
    public static final ReadDescription<Mitnahme> READ_DESCRIPTION = stream -> {
        final ProfilCode mitnahmeTypeCode = ProfilCode.READ_DESCRIPTION.read(stream);
        final INT1 mitnahmeAnzahl = INT1.READ_DESCRIPTION.read(stream);
        return new Mitnahme(mitnahmeTypeCode, mitnahmeAnzahl);
    };

    final ProfilCode mitnahmeTypeCode;
    final INT1 mitnahmeAnzahl;

    @Override
    public ReadDescription<Mitnahme> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        mitnahmeTypeCode.write(stream);
        mitnahmeAnzahl.write(stream);
    }

    @Override
    public int getLength() {
        return mitnahmeTypeCode.getLength() + mitnahmeAnzahl.getLength();
    }
}
