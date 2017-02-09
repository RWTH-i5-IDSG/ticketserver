package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;
import de.rwth.idsg.barti.core.datatypes.basic.INT4;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class CACHR implements CHR<CACHR> {
    final INT4 fourZeroes;
    final CAR caOwnerCAR;

    public static final ReadDescription<CACHR> READ_DESCRIPTION = stream -> {
        final INT4 fourZeroes = INT4.READ_DESCRIPTION.read(stream);
        final CAR caOwnerCAR = CAR.READ_DESCRIPTION.read(stream);
        return new CACHR(fourZeroes, caOwnerCAR);
    };

    @Override public ReadDescription<CACHR> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        fourZeroes.write(stream);
        caOwnerCAR.write(stream);
    }
}
