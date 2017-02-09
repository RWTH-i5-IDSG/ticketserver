package de.rwth.idsg.barti.core.datatypes;

import de.rwth.idsg.barti.core.MyInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ReadDescription<T> {
    T read(final MyInputStream stream) throws IOException;

    default T read(final byte[] bytes) throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final T value = read(new MyInputStream(stream));
        if (stream.available() > 0) {
            throw new IOException("Bytes not fully consumed!");
        }
        return value;
    }
}
