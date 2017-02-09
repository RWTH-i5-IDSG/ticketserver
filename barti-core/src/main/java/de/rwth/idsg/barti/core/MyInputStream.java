package de.rwth.idsg.barti.core;

import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInputStream {
    final ReadableByteChannel channel;
    final ByteOrder byteOrder;
    final Charset charset;

    public MyInputStream(final InputStream inputStream) {
        this(
                Channels.newChannel(new DataInputStream(new BufferedInputStream(inputStream))),
                Environment.BYTE_ORDER,
                Environment.ISO_8859_15
        );
    }

    private void readFully(final ByteBuffer buffer) throws IOException {
        final int read = channel.read(buffer);
        if (read != buffer.capacity()) {
            throw new IOException(String.format(
                    "Attempted to read %s bytes, but could only read %s!",
                    buffer.capacity(),
                    read
            ));
        }
        buffer.rewind();
    }

    public void readTag(final byte tag) throws IOException {
        final byte read = read1ByteSigned();
        if (read != tag) {
            throw new IllegalStateException(String.format("Expected to read %s, but found %s!", tag, read));
        }
    }

    public byte[] readBytes(final int length) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.array();
    }

    public byte read1ByteSigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.get();
    }

    public short read1ByteUnsigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(byteOrder);
        readFully(buffer);
        return (short) UnsignedBytes.toInt(buffer.get());
    }

    public short read2ByteSigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.getShort();
    }

    public int read2ByteUnsigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        readFully(buffer);
        final byte hi = buffer.get();
        final byte lo = buffer.get();
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return ((hi << 8) & 0xFF00) | (lo & 0xFF);
        }
        // LITTLE ENDIAN
        return ((lo << 8) & 0xFF00) | (hi & 0xFF);
    }

    public int read3ByteSigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.order(byteOrder);
        readFully(buffer);
        final byte hi = buffer.get();
        final byte mi = buffer.get();
        final byte lo = buffer.get();
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (hi << 16) | ((mi << 8) & 0xFF00) | (lo & 0xFF);
        }
        // LITTLE ENDIAN
        return (lo << 16) | ((mi << 8) & 0xFF00) | (hi & 0xFF);
    }

    public int read3ByteUnsigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.order(byteOrder);
        readFully(buffer);
        final byte hi = buffer.get();
        final byte mi = buffer.get();
        final byte lo = buffer.get();
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return ((hi << 16) & 0xFF0000) | ((mi << 8) & 0xFF00) | (lo & 0xFF);
        }
        // LITTLE ENDIAN
        return ((lo << 16) & 0xFF0000) | ((mi << 8) & 0xFF00) | (hi & 0xFF);
    }

    public int read4ByteSigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.getInt();
    }

    public long read4ByteUnsigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(byteOrder);
        readFully(buffer);
        return UnsignedInts.toLong(buffer.getInt());
    }

    public long read8ByteSigned() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.getLong();
    }

    public float readFloat() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.getFloat();
    }

    public double readDouble() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.order(byteOrder);
        readFully(buffer);
        return buffer.getDouble();
    }

    public char[] readChars(final int count) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(count);
        buffer.order(byteOrder);
        readFully(buffer);
        return charset.decode(buffer).array();
    }
}
