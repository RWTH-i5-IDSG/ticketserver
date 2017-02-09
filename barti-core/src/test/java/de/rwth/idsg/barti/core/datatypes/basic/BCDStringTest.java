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
public class BCDStringTest {
    @Value
    static class TestData {
        int decimal;
        byte composed;
        byte bcdhi, bcdlo;
    }

    private static final ImmutableList<TestData> TEST_DATA = ImmutableList.of(
            new TestData(0, (byte) 0x00, (byte) 0, (byte) 0),
            new TestData(1, (byte) 0x01, (byte) 0, (byte) 1),
            new TestData(10, (byte) 0x10, (byte) 1, (byte) 0),
            new TestData(11, (byte) 0x11, (byte) 1, (byte) 1),
            new TestData(99, (byte) 0x99, (byte) 9, (byte) 9),
            new TestData(9, (byte) 0x09, (byte) 0, (byte) 9),
            new TestData(90, (byte) 0x90, (byte) 9, (byte) 0),
            new TestData(25, (byte) 0x25, (byte) 2, (byte) 5),
            new TestData(52, (byte) 0x52, (byte) 5, (byte) 2),
            new TestData(31, (byte) 0x31, (byte) 3, (byte) 1),
            new TestData(76, (byte) 0x76, (byte) 7, (byte) 6)
    );

    @Test
    public void read() throws Exception {
        for (final TestData testData : TEST_DATA) {
            readTest(testData);
        }
    }

    private void readTest(final TestData testData) throws IOException {
        final byte[] bytes = {testData.composed};
        final BCDString read1 = BCDString.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(
                bytes)));
        assertEquals(testData.composed, read1.getComposed());
        assertEquals(testData.decimal, read1.getDecimal());
        assertEquals(testData.bcdhi, read1.getHi());
        assertEquals(testData.bcdlo, read1.getLo());
        final BCDString read2 = BCDString.READ_DESCRIPTION.read(bytes);
        assertEquals(testData.composed, read2.getComposed());
        assertEquals(testData.decimal, read2.getDecimal());
        assertEquals(testData.bcdhi, read2.getHi());
        assertEquals(testData.bcdlo, read2.getLo());
    }

    @Test
    public void write() throws Exception {
        for (final TestData testData : TEST_DATA) {
            writeTest(testData);
        }
    }

    private void writeTest(final TestData testData) throws IOException {
        {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
                BCDString.fromDecimal(testData.decimal).write(myOutputStream);
            }
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            assertArrayEquals(new byte[]{testData.composed}, bytes);
        }
        {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
                BCDString.fromComposed(testData.composed).write(myOutputStream);
            }
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            assertArrayEquals(new byte[]{testData.composed}, bytes);
        }
        {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (final MyOutputStream myOutputStream = new MyOutputStream(byteArrayOutputStream)) {
                new BCDString(testData.bcdhi, testData.bcdlo).write(myOutputStream);
            }
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            assertArrayEquals(new byte[]{testData.composed}, bytes);
        }
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(1, new BCDString((byte) 0x0, (byte) 0x0).getLength());
    }

    @Test
    public void getValue() throws Exception {
        for (final TestData testData : TEST_DATA) {
            assertEquals(testData.decimal, new BCDString(testData.bcdhi, testData.bcdlo).getDecimal());
            assertEquals(testData.composed, new BCDString(testData.bcdhi, testData.bcdlo).getComposed());
        }
    }

    @Test
    public void testFromTo() throws Exception {
        for (final TestData testData : TEST_DATA) {
            {
                final BCDString fromComposed = BCDString.fromComposed(testData.composed);
                final byte decimal = fromComposed.getDecimal();
                assertEquals(testData.decimal, decimal);
            }
            {
                final BCDString fromDecimal = BCDString.fromDecimal(testData.decimal);
                final byte decimal = fromDecimal.getDecimal();
                assertEquals(testData.decimal, decimal);
            }
            {
                final BCDString fromComposed = BCDString.fromComposed(testData.composed);
                final byte composed = fromComposed.getComposed();
                assertEquals(testData.composed, composed);
            }
            {
                final BCDString fromDecimal = BCDString.fromDecimal(testData.decimal);
                final byte composed = fromDecimal.getComposed();
                assertEquals(testData.composed, composed);
            }
        }
    }
}
