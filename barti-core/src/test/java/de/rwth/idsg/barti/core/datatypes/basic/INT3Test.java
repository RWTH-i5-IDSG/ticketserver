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
public class INT3Test {
    @Value
    static class TestData {
        int value;
        byte[] bytes;
    }

    private static final ImmutableList<TestData> TEST_DATA = ImmutableList.of(
            new TestData(0, new byte[]{0, 0, 0}),
            new TestData(1, new byte[]{0, 0, 1}),
            new TestData(2, new byte[]{0, 0, 2}),
            new TestData(0xFF, new byte[]{0, 0, (byte) 0xFF}),
            new TestData(0xFF00, new byte[]{0, (byte) 0xFF, 0}),
            new TestData(0xFF0000, new byte[]{(byte) 0xFF, 0, 0}),
            new TestData(0x0100, new byte[]{0, 1, 0}),
            new TestData(0x010000, new byte[]{1, 0, 0})
    );

    @Test
    public void read() throws Exception {
        for (final TestData testData : TEST_DATA) {
            readTest(testData.bytes, testData.value);
        }
    }

    private void readTest(final byte[] bytes, final int expected) throws IOException {
        final INT3 read1 = INT3.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(bytes)));
        final int value1 = read1.getValue();
        assertEquals(expected, value1);
        final INT3 read2 = INT3.READ_DESCRIPTION.read(bytes);
        final int value2 = read2.getValue();
        assertEquals(expected, value2);
    }

    @Test
    public void write() throws Exception {
        for (final TestData testData : TEST_DATA) {
            writeTest(testData.value, testData.bytes);
        }
    }

    private void writeTest(final int value, final byte[] expected) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
            new INT3(value).write(myOutputStream);
        }
        final byte[] bytes = byteArrayOutputStream.toByteArray();
        assertArrayEquals(expected, bytes);
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(3, new INT3(0).getLength());
    }

    @Test
    public void getValue() throws Exception {
        for (final TestData testData : TEST_DATA) {
            assertEquals(testData.value, new INT3(testData.value).getValue());
        }
    }
}
