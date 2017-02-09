package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.PrintableStringWithPrecedingOneByteSize;

import java.io.IOException;

/**
 * 5-81
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil
        implements Data<SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil> {

    public static final byte TAG = (byte) 0x85;

    public static final ReadDescription<SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil>
            READ_DESCRIPTION = stream -> {
        stream.readTag(TAG);
        final PrintableStringWithPrecedingOneByteSize freitext
                = PrintableStringWithPrecedingOneByteSize.READ_DESCRIPTION.read(stream);
        return new SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil(freitext);
    };

    final PrintableStringWithPrecedingOneByteSize freitext;

    @Override
    public ReadDescription<SeparateDatenBerechtigungEFSStatischerProduktspezifischerTeil> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeByte(TAG);
        freitext.write(stream);
    }

    @Override
    public int getLength() {
        return 1 + freitext.getLength();
    }
}
