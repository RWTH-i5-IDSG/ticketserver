package de.rwth.idsg.barti.core.datatypes.basic;

import com.google.common.collect.ImmutableList;
import de.rwth.idsg.barti.core.MyInputStream;
import de.rwth.idsg.barti.core.MyOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class DatefTest {
    @Value
    @RequiredArgsConstructor
    static class TestData {
        LocalDate date;
        byte[] bcdString;

        TestData(final String dateString, final byte[] bcdString) {
            this(LocalDate.parse(dateString, DateTimeFormat.forPattern("YYYY-MM-dd")), bcdString);
        }
    }

    private static final ImmutableList<TestData> TEST_DATA = ImmutableList.of(
            new TestData("2000-01-01", new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x01, (byte) 0x01}),
            new TestData("2001-01-01", new byte[]{(byte) 0x20, (byte) 0x01, (byte) 0x01, (byte) 0x01}),
            new TestData("2000-12-01", new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x12, (byte) 0x01}),
            new TestData("2000-12-31", new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x12, (byte) 0x31}),
            new TestData("2000-05-31", new byte[]{(byte) 0x20, (byte) 0x00, (byte) 0x05, (byte) 0x31}),
            new TestData("2010-01-01", new byte[]{(byte) 0x20, (byte) 0x10, (byte) 0x01, (byte) 0x01}),
            new TestData("2020-01-01", new byte[]{(byte) 0x20, (byte) 0x20, (byte) 0x01, (byte) 0x01}),
            new TestData("2030-01-01", new byte[]{(byte) 0x20, (byte) 0x30, (byte) 0x01, (byte) 0x01}),
            new TestData("2040-01-01", new byte[]{(byte) 0x20, (byte) 0x40, (byte) 0x01, (byte) 0x01})
    );

    @Test
    public void read() throws Exception {
        for (final TestData testData : TEST_DATA) {
            readTest(testData);
        }
    }

    private void readTest(final TestData testData) throws IOException {
        final Datef read1 = Datef.READ_DESCRIPTION.read(new MyInputStream(new ByteArrayInputStream(
                testData.bcdString)));
        assertEquals(testData.date, read1.getDate());
        final Datef read2 = Datef.READ_DESCRIPTION.read(testData.bcdString);
        assertEquals(testData.date, read2.getDate());
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
                new Datef(testData.date).write(myOutputStream);
            }
            final byte[] bytes = byteArrayOutputStream.toByteArray();
            assertArrayEquals(testData.bcdString, bytes);
        }
    }

    @Test
    public void getLength() throws Exception {
        assertEquals(4, new Datef(LocalDate.now()).getLength());
    }
}
