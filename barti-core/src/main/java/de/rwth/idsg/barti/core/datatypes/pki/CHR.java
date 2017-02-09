package de.rwth.idsg.barti.core.datatypes.pki;

import de.rwth.idsg.barti.core.datatypes.Data;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface CHR<T extends CHR> extends Data<T> {
    int CHR_LENGTH = 12;

    @Override default int getLength() {
        return CHR_LENGTH;
    }
}
