package de.rwth.idsg.barti.core.datatypes.basic;

import com.google.common.collect.ImmutableList;
import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.MyOutputStream;
import lombok.Value;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class INT4Test {
    @Value
    static class TestData {
        long value;
        byte[] bytes;
    }

    private static final ImmutableList<TestData> TEST_DATA = ImmutableList.of(
            new TestData(0L, new byte[]{0, 0, 0, 0}),
            new TestData(1L, new byte[]{0, 0, 0, 1}),
            new TestData(2L, new byte[]{0, 0, 0, 2}),
            new TestData(0xFFL, new byte[]{0, 0, 0, (byte) 0xFF}),
            new TestData(0xFF00L, new byte[]{0, 0, (byte) 0xFF, 0}),
            new TestData(0xFF0000L, new byte[]{0, (byte) 0xFF, 0, 0}),
            new TestData(0xFF000000L, new byte[]{(byte) 0xFF, 0, 0, 0}),
            new TestData(0x0100L, new byte[]{0, 0, 1, 0}),
            new TestData(0x010000L, new byte[]{0, 1, 0, 0}),
            new TestData(0x01000000L, new byte[]{1, 0, 0, 0})
    );

    @Test
    public void read() throws Exception {
        for (final TestData testData : TEST_DATA) {
            readTest(testData.bytes, testData.value);
        }
    }

    private void readTest(final byte[] bytes, final long expected) throws IOException {
        final INT4 read1 = INT4.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(bytes)));
        final long value1 = read1.getValue();
        assertEquals(expected, value1);
        final INT4 read2 = INT4.READ_DESCRIPTION.read(bytes);
        final long value2 = read2.getValue();
        assertEquals(expected, value2);
    }

    @Test
    public void write() throws Exception {
        for (final TestData testData : TEST_DATA) {
            writeTest(testData.value, testData.bytes);
        }
    }

    private void writeTest(final long value, final byte[] expected) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
            new INT4(value).write(myOutputStream);
        }
        final byte[] bytes = byteArrayOutputStream.toByteArray();
        assertArrayEquals(expected, bytes);
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(4, new INT4(0).getLength());
    }

    @Test
    public void getValue() throws Exception {
        for (final TestData testData : TEST_DATA) {
            assertEquals(testData.value, new INT4(testData.value).getValue());
        }
    }
}
