package de.rwth.idsg.barti.core.datatypes.basic;

import de.rwth.idsg.barti.core.MyOutputStream;
import de.rwth.idsg.barti.core.datatypes.Data;
import de.rwth.idsg.barti.core.datatypes.ReadDescription;

import java.io.IOException;

/**
 * BCDString ist eine Kette von Binaer Codierten Dezimalziffern (BCD). Pro Dezimalziffer werden 4 Bits (=ein Halb-Byte)
 * benötigt.
 * N gibt die Anzahl Bytes an, die benötigt werden.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@lombok.Data
public class BCDString implements Data {

    public static final ReadDescription<BCDString> READ_DESCRIPTION = stream -> fromComposed(stream.read1ByteSigned());

    final byte hi, lo;

    public byte getDecimal() {
        return (byte) (hi * 10 + lo);
    }

    public byte getComposed() {
        return (byte) (hi << 4 | lo);
    }

    public static BCDString fromComposed(final byte b) {
        return new BCDString((byte) ((b >> 4) & 0x0F), (byte) (b & 0x0F));
    }

    public static BCDString fromDecimal(final int i) {
        return new BCDString((byte) ((i / 10) & 0x0F), (byte) (i % 10 & 0x0F));
    }

    @Override
    public void write(final MyOutputStream stream) throws IOException {
        stream.write1ByteSigned(getComposed());
    }

    @Override
    public ReadDescription<BCDString> getReadDescription() {
        return READ_DESCRIPTION;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public String toString() {
        return Integer.toString(getDecimal());
    }
}
