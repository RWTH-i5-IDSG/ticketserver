package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberFour;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;

import java.io.IOException;

/**
 * 5-5
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class BerechtigungID implements Data<BerechtigungID> {
    public static final ReadDescription<BerechtigungID> READ_DESCRIPTION = stream -> {
        final ReferenceNumberFour berechtigungNummer = ReferenceNumberFour.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo organisationIDOrganisationsNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        return new BerechtigungID(berechtigungNummer, organisationIDOrganisationsNummer);
    };

    final ReferenceNumberFour berechtigungNummer;
    final ReferenceNumberTwo organisationIDOrganisationsNummer;

    @Override
    public ReadDescription<BerechtigungID> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        berechtigungNummer.write(stream);
        organisationIDOrganisationsNummer.write(stream);
    }

    @Override
    public int getLength() {
        return berechtigungNummer.getLength() + organisationIDOrganisationsNummer.getLength();
    }
}
