package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.ReferenceNumberThree;
import de.rwth.idsg.barti.core.datatypes.basic.SequenceNumberFour;

import java.io.IOException;

/**
 * 5-12
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class NmTransaktionID implements Data<NmTransaktionID> {
    public static final ReadDescription<NmTransaktionID> READ_DESCRIPTION = stream -> {
        final SequenceNumberFour samSequenznummer = SequenceNumberFour.READ_DESCRIPTION.read(stream);
        final ReferenceNumberThree samIDSamNummer = ReferenceNumberThree.READ_DESCRIPTION.read(stream);
        return new NmTransaktionID(samSequenznummer, samIDSamNummer);
    };

    final SequenceNumberFour samSequenznummer;
    final ReferenceNumberThree samIDSamNummer;

    @Override
    public ReadDescription<NmTransaktionID> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        samSequenznummer.write(stream);
        samIDSamNummer.write(stream);
    }

    @Override
    public int getLength() {
        return samSequenznummer.getLength() + samIDSamNummer.getLength();
    }
}
