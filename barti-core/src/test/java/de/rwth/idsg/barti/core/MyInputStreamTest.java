package de.rwth.idsg.barti.core;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for de.rwth.idsg.barti.core.MyInputStream.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MyInputStreamTest {

    static final int TRIES = 100;

    @Test
    public void testReadTag() throws Exception {
        final int bytes = 1 << Byte.BYTES * 8;
        final ByteBuffer buf = ByteBuffer.allocate(bytes);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < bytes; ++i) {
            buf.put((byte) i);
        }
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(buf.array()));
        for (int i = 0; i < bytes; ++i) {
            myInputStream.readTag((byte) i);
        }
    }

    @Test
    public void testReadBytes() throws Exception {
        final int capacity = (TRIES * (TRIES + 1)) / 2;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        int currentByte = Byte.MIN_VALUE;
        for (int i = 0; i < capacity; ++i) {
            buf.put((byte) currentByte++);
            if (currentByte >= Byte.MAX_VALUE) {
                currentByte = Byte.MIN_VALUE;
            }
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = 0; i < TRIES; ++i) {
            final byte[] bytes = myInputStream.readBytes(i);
            final int offset = (i * (i - 1)) / 2;
            for (int j = 0; j < i; ++j) {
                assertThat(bytes[j], is(byteArray[offset + j]));
            }
        }
    }

    @Test
    public void testReadChars() throws Exception {
        final int capacity = (TRIES * (TRIES + 1)) / 2;
        final CharBuffer buf = CharBuffer.allocate(capacity);
        int currentChar = 32;
        for (int i = 0; i < capacity; ++i) {
            buf.put((char) currentChar++);
            if (currentChar >= 127) {
                currentChar = 160;
            }
            if (currentChar >= 256) {
                currentChar = 32;
            }
        }
        buf.rewind();
        final char[] charArray = buf.array();
        final ByteBuffer encoded = Environment.ISO_8859_15.encode(buf);
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(encoded.array()));
        for (int i = 0; i < TRIES; ++i) {
            final char[] chars = myInputStream.readChars(i);
            final int offset = (i * (i - 1)) / 2;
            for (int j = 0; j < i; ++j) {
                assertThat(chars[j], is(charArray[offset + j]));
            }
        }
    }

    @Test
    public void testRead1ByteSigned() throws Exception {
        final int capacity = 1 << 8;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        byte currentByte = Byte.MIN_VALUE;
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; ++i) {
            buf.put(currentByte++);
        }
        buf.rewind();
        assertThat(currentByte, is(Byte.MIN_VALUE));
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = 0; i < capacity; ++i) {
            final byte b = myInputStream.read1ByteSigned();
            assertThat(b, is(currentByte++));
        }
    }

    @Test
    public void testRead1ByteUnsigned() throws Exception {
        final int capacity = 1 << 8;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int currentByte = 0; currentByte < capacity; ++currentByte) {
            buf.put((byte) (currentByte & 0xFF));
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = 0; i < capacity; ++i) {
            final short s = myInputStream.read1ByteUnsigned();
            assertThat(s, is((short) (byteArray[i] & 0xFF)));
        }
    }

    @Test
    public void testRead2ByteSigned() throws Exception {
        final int maxValue = 1 << 16;
        final int capacity = maxValue * 2;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        short currentShort = Short.MIN_VALUE;
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; ++i) {
            buf.putShort(currentShort++);
        }
        buf.rewind();
        assertThat(currentShort, is(Short.MIN_VALUE));
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = Short.MIN_VALUE; i <= Short.MAX_VALUE; ++i) {
            final short s = myInputStream.read2ByteSigned();
            assertThat(s, is(currentShort++));
        }
    }

    @Test
    public void testRead2ByteUnsigned() throws Exception {
        final int maxValue = 1 << 16;
        final int capacity = maxValue * 2;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int currentShort = 0; currentShort < maxValue; ++currentShort) {
            buf.putShort((short) (currentShort & 0xFFFF));
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int currentShort = 0; currentShort < maxValue; ++currentShort) {
            final int s = myInputStream.read2ByteUnsigned();
            assertThat(s, is((currentShort & 0xFFFF)));
        }
    }

    @Test
    public void testRead3ByteSigned() throws Exception {
        final int maxValue = 1 << 24;
        final int capacity = maxValue * 3;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < maxValue; ++i) {
            buf.put((byte) ((i >> 16) & 0xFF));
            buf.put((byte) ((i >> 8) & 0xFF));
            buf.put((byte) (i & 0xFF));
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = 0; i < maxValue; ++i) {
            final int read = myInputStream.read3ByteSigned();
            // shift 8 to the left and back again to get a 0xFF head in case the number read is negative
            assertThat(read, is((i << 8) >> 8));
        }
    }

    @Test
    public void testRead3ByteUnsigned() throws Exception {
        final int maxValue = 1 << 24;
        final int capacity = maxValue * 3;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < maxValue; ++i) {
            buf.put((byte) ((i >> 16) & 0xFF));
            buf.put((byte) ((i >> 8) & 0xFF));
            buf.put((byte) (i & 0xFF));
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int i = 0; i < maxValue; ++i) {
            final int read = myInputStream.read3ByteUnsigned();
            assertThat(read, is(i & 0x00FF_FFFF));
        }
    }

    @Test
    public void testRead4ByteSigned() throws Exception {
        final int[] testInts = new int[]{0, 1, -1, 2, -2, 3, -3, Byte.MIN_VALUE, Byte.MAX_VALUE, Short.MIN_VALUE, Short
                .MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, 0xFFFF_FFFF, 0xF0F0_F0F0, 0x0F0F_0F0F, 0x0FF0_0FF0,
                0xF00F_F00F};
        final int capacity = testInts.length * 4;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (int testInt : testInts) {
            buf.putInt(testInt);
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (int testInt : testInts) {
            final int read = myInputStream.read4ByteSigned();
            assertThat(read, is(testInt));
        }
    }

    @Test
    public void testRead4ByteUnsigned() throws Exception {
        final long[] testLongs = new long[]{0L, 1L, 2L, 3L, Byte.MAX_VALUE, Short
                .MAX_VALUE, Integer.MAX_VALUE, 0xFFFF_FFFFL, 0xF0F0_F0F0L, 0x0F0F_0F0FL, 0x0FF0_0FF0L,
                0xF00F_F00FL};
        final int capacity = testLongs.length * 4;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (long testLong : testLongs) {
            buf.putInt((int) testLong);
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (long testLong : testLongs) {
            final long read = myInputStream.read4ByteUnsigned();
            assertThat(read, is(testLong));
        }
    }

    @Test
    public void testRead8ByteSigned() throws Exception {
        final long[] testLongs = new long[]{0L, 1L, -1L, 2L, -2L, 3L, -3L, Byte.MIN_VALUE, Byte.MAX_VALUE, Short
                .MIN_VALUE,
                Short.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, 0xFFFF_FFFFL, 0xF0F0_F0F0L, 0x0F0F_0F0FL,
                0x0FF0_0FF0L, 0xF00F_F00FL, 0xFFFF_FFFF_FFFF_FFFFL, 0xF0F0_F0F0_F0F0_F0F0L, 0x0F0F_0F0F_0F0F_0F0FL,
                0x0FF0_0FF0_0FF0_0FF0L, 0x0FF0_0FF0_0FF0_0FF0L};
        final int capacity = testLongs.length * 8;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (long testLong : testLongs) {
            buf.putLong(testLong);
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (long testLong : testLongs) {
            final long read = myInputStream.read8ByteSigned();
            assertThat(read, is(testLong));
        }
    }

    @Test
    public void testReadFloat() throws Exception {
        final float[] testFloats = new float[]{0f, 1f, -1f, 2f, -2f, 3f, -3f, Float.MIN_VALUE, Float.MAX_VALUE, Byte
                .MIN_VALUE, Byte.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE};
        final int capacity = testFloats.length * 4;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (float testFloat : testFloats) {
            buf.putFloat(testFloat);
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (float testFloat : testFloats) {
            final float read = myInputStream.readFloat();
            assertThat(read, is(testFloat));
        }
    }

    @Test
    public void testReadDouble() throws Exception {
        final double[] testDoubles = new double[]{0d, 1d, -1d, 2d, -2d, 3d, -3d, Float.MIN_VALUE, Float.MAX_VALUE, Byte
                .MIN_VALUE, Byte.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE};
        final int capacity = testDoubles.length * 8;
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.order(ByteOrder.BIG_ENDIAN);
        for (double testDouble : testDoubles) {
            buf.putDouble(testDouble);
        }
        buf.rewind();
        final byte[] byteArray = buf.array();
        final MyInputStream myInputStream = new MyInputStream(new ByteArrayInputStream(byteArray));
        for (double testDouble : testDoubles) {
            final double read = myInputStream.readDouble();
            assertThat(read, is(testDouble));
        }
    }
}
