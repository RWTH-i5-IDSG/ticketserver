package de.rwth.idsg.barti.core.datatypes.composite;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.enums.TransportmittelKategorieCode;

import java.io.IOException;

/**
 * 5-49
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class TransaktionProduktspezifischerTeil implements
        Data<TransaktionProduktspezifischerTeil> {

    public static final byte TAG = (byte) 0x8a;
    public static final byte LENGTH = (byte) 1;

    public static final ReadDescription<TransaktionProduktspezifischerTeil> READ_DESCRIPTION = stream -> {
        stream.readTag(TAG);
        stream.readTag(LENGTH);
        final TransportmittelKategorieCode efsVerkehrsmittelKategorieCode = TransportmittelKategorieCode
                .READ_DESCRIPTION.read(stream);
        return new TransaktionProduktspezifischerTeil(efsVerkehrsmittelKategorieCode);
    };

    final TransportmittelKategorieCode efsVerkehrsmittelKategorieCode;

    @Override
    public ReadDescription<TransaktionProduktspezifischerTeil> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.writeByte(TAG);
        stream.writeByte(LENGTH);
        efsVerkehrsmittelKategorieCode.write(stream);
    }

    @Override
    public int getLength() {
        return 2 + efsVerkehrsmittelKategorieCode.getLength();
    }
}
