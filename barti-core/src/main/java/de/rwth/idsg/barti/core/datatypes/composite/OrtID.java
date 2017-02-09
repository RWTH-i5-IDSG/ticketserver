package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberThree;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.enums.OrtsTypCode;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * 5-11
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
@lombok.Data
public class OrtID implements Data<OrtID> {
    public static final ReadDescription<OrtID> READ_DESCRIPTION = stream -> {
        final OrtsTypCode ortTypCode = OrtsTypCode.READ_DESCRIPTION.read(stream);
        final ReferenceNumberThree ortNummer = ReferenceNumberThree.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo organisationIDOrganisationsNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        return new OrtID(ortTypCode, ortNummer, organisationIDOrganisationsNummer);
    };

    final OrtsTypCode ortTypCode;
    final ReferenceNumberThree ortNummer;
    ReferenceNumberTwo organisationIDOrganisationsNummer;

    @Override
    public ReadDescription<OrtID> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        ortTypCode.write(stream);
        ortNummer.write(stream);
        organisationIDOrganisationsNummer.write(stream);
    }

    @Override
    public int getLength() {
        return ortTypCode.getLength() + ortNummer.getLength() + organisationIDOrganisationsNummer.getLength();
    }
}
