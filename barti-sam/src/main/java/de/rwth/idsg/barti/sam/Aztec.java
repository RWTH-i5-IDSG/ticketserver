package de.rwth.idsg.barti.sam;

import com.google.common.base.Charsets;
import com.google.zxing.aztec.encoder.AztecCode;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@UtilityClass
@Log4j2
public class Aztec {

    private static final int ECC_PERCENT = 23;
    private static final int LAYERS = 13;
    private static final int MODULES = 71;

    private static final byte[] EMPTY = new byte[]{};
    private static final byte[] HEADER = createHeader();
    private static final byte[] MAGIC_NUMBERS = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    /**
     * Converts signature into Aztec Code PNG where each bit of the code is represented via 16x16 pixels in the
     * PNG and writes the PNG into the output stream given.
     *
     * @param signature signature to convert into aztec code png
     * @param out       output stream to write the data to
     * @throws IOException passed through
     */
    public static void createPNG(final byte[] signature, final OutputStream out) throws IOException {
        final AztecCode aztecCode = Encoder.encode(signature, ECC_PERCENT, LAYERS);
        createPNG(aztecCode.getMatrix(), out);
    }

    @SuppressWarnings("checkstyle:innerassignment")
    private static void createPNG(final BitMatrix bitMatrix, final OutputStream out) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final DataOutputStream file = new DataOutputStream(new BufferedOutputStream(out))) {
            file.write(MAGIC_NUMBERS);

            try (final DeflaterOutputStream deflate = new DeflaterOutputStream(bos)) {
                final byte[] targetRow = new byte[MODULES * 2];
                for (int i = 0; i < MODULES; ++i) {
                    final BitArray row = bitMatrix.getRow(i, null);
                    for (int y = 0; y < MODULES; ++y) {
                        targetRow[2 * y] = targetRow[2 * y + 1] = row.get(y) ? 0x00 : (byte) 0xFF;
                    }
                    for (int t = 0; t < 16; ++t) {
                        // filter-type byte: 0
                        deflate.write(0);
                        // actual row
                        deflate.write(targetRow);
                    }
                }
            }

            block(file, HEADER, "IHDR");
            block(file, bos.toByteArray(), "IDAT");
            block(file, EMPTY, "IEND");
        }
    }

    private static byte[] createHeader() {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final DataOutputStream out = new DataOutputStream(bos)) {
            // 4 byte width
            out.writeInt(MODULES * 16);
            // 4 byte height
            out.writeInt(MODULES * 16);
            // 1 byte bit depth
            out.writeByte(1);
            // 1 byte color type
            out.writeByte(0);
            // 1 byte compression method
            out.writeByte(0);
            // 1 byte filter method
            out.writeByte(0);
            // 1 byte interlace method
            out.writeByte(0);
            return bos.toByteArray();
        } catch (final IOException e) {
            throw new Error(e);
        }
    }

    private static void block(final DataOutputStream file, final byte[] chunk, final String type) throws IOException {
        file.writeInt(chunk.length);
        final byte[] typeBytes = type.getBytes(Charsets.US_ASCII);
        file.write(typeBytes, 0, 4);
        file.write(chunk);
        final CRC32 crc32 = new CRC32();
        crc32.update(typeBytes, 0, 4);
        crc32.update(chunk);
        file.writeInt((int) crc32.getValue());
    }
}
