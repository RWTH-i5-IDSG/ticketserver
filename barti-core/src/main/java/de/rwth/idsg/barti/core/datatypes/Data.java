package de.rwth.idsg.barti.core.datatypes;

import de.rwth.idsg.barti.core.MyOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface Data<T> {
    ReadDescription<T> getReadDescription();

    void write(final MyOutputStream stream) throws IOException;

    int getLength();

    default byte[] write() {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (final MyOutputStream stream = new MyOutputStream(bytes)) {
            write(stream);
        } catch (final IOException e) {
            throw new Error(e);
        }
        return bytes.toByteArray();
    }
}
