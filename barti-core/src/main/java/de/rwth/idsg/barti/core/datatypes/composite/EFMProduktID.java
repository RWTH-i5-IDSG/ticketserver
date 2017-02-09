package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;

import java.io.IOException;

/**
 * 5-4
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class EFMProduktID implements Data<EFMProduktID> {
    public static final ReadDescription<EFMProduktID> READ_DESCRIPTION = stream -> {
        final ReferenceNumberTwo produktNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo organisationIDOrganisationsNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        return new EFMProduktID(produktNummer, organisationIDOrganisationsNummer);
    };

    final ReferenceNumberTwo produktNummer;
    final ReferenceNumberTwo organisationIDOrganisationsNummer;

    @Override
    public ReadDescription<EFMProduktID> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        produktNummer.write(stream);
        organisationIDOrganisationsNummer.write(stream);
    }

    @Override
    public int getLength() {
        return produktNummer.getLength() + organisationIDOrganisationsNummer.getLength();
    }
}
