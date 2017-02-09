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
public class INT1Test {
    @Value
    static class TestData {
        short value;
        byte[] bytes;
    }

    private static final ImmutableList<TestData> TEST_DATA = ImmutableList.of(
            new TestData((short) 0, new byte[]{0}),
            new TestData((short) 1, new byte[]{1}),
            new TestData((short) 2, new byte[]{2}),
            new TestData((short) 3, new byte[]{3}),
            new TestData((short) 0b01010101, new byte[]{0b01010101}),
            new TestData((short) 0b10101010, new byte[]{(byte) 0b10101010}),
            new TestData((short) 0b00110011, new byte[]{0b00110011}),
            new TestData((short) 0xFF, new byte[]{(byte) 0xFF})
    );

    @Test
    public void read() throws Exception {
        for (final TestData testData : TEST_DATA) {
            readTest(testData.bytes, testData.value);
        }
    }

    private void readTest(final byte[] bytes, final short expected) throws IOException {
        final INT1 read1 = INT1.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(bytes)));
        final short value1 = read1.getValue();
        assertEquals(expected, value1);
        final INT1 read2 = INT1.READ_DESCRIPTION.read(bytes);
        final short value2 = read2.getValue();
        assertEquals(expected, value2);
    }

    @Test
    public void write() throws Exception {
        for (final TestData testData : TEST_DATA) {
            writeTest(testData.value, testData.bytes);
        }
    }

    private void writeTest(final short value, final byte[] expected) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
            new INT1(value).write(myOutputStream);
        }
        final byte[] bytes = byteArrayOutputStream.toByteArray();
        assertArrayEquals(expected, bytes);
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(1, new INT1((short) 0).getLength());
    }

    @Test
    public void getValue() throws Exception {
        for (final TestData testData : TEST_DATA) {
            assertEquals(testData.value, new INT1(testData.value).getValue());
        }
    }
}
