package de.rwth.idsg.barti.core.datatypes.composite;

import com.google.common.collect.ImmutableList;
import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberTwo;
import de.rwth.idsg.barti.core.datatypes.enums.TerminalTypCode;
import lombok.AllArgsConstructor;

import java.io.IOException;

import static java8.util.stream.StreamSupport.stream;

/**
 * 5-3
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@AllArgsConstructor
@lombok.Data
public class TerminalID implements Data<TerminalID> {
    public static final ReadDescription<TerminalID> READ_DESCRIPTION = stream -> {
        final TerminalTypCode terminalTypCode = TerminalTypCode.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo terminalNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        final ReferenceNumberTwo organisationIDOrganisationsNummer = ReferenceNumberTwo.READ_DESCRIPTION.read(stream);
        return new TerminalID(terminalTypCode, terminalNummer, organisationIDOrganisationsNummer);
    };

    final TerminalTypCode terminalTypCode;
    final ReferenceNumberTwo terminalNummer;
    ReferenceNumberTwo organisationIDOrganisationsNummer;

    @Override
    public ReadDescription<TerminalID> getReadDescription() {
        return READ_DESCRIPTION;
    }

    public ImmutableList<Data<?>> getElements() {
        return ImmutableList.<Data<?>>builder()
                .add(terminalTypCode)
                .add(terminalNummer)
                .add(organisationIDOrganisationsNummer)
                .build();
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        for (final Data<?> data : getElements()) {
            data.write(stream);
        }
    }

    @Override
    public int getLength() {
        return stream(getElements()).mapToInt(Data::getLength).sum();
    }
}
