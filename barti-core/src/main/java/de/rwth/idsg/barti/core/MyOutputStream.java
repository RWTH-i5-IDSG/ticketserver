package de.rwth.idsg.barti.core;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class MyOutputStream implements Closeable {
    final BufferedOutputStream lowerLayer;
    final WritableByteChannel channel;
    final ByteOrder byteOrder;
    final Charset charset;

    public MyOutputStream(final OutputStream outputStream,
                          final ByteOrder byteOrder,
                          final Charset charset
    ) {
        this.lowerLayer = new BufferedOutputStream(outputStream);
        this.channel = Channels.newChannel(this.lowerLayer);
        this.byteOrder = byteOrder;
        this.charset = charset;
    }

    public MyOutputStream(final OutputStream outputStream) {
        this(outputStream, Environment.BYTE_ORDER, Environment.ISO_8859_15);
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
        this.lowerLayer.close();
    }

    private void writeFully(final ByteBuffer buffer) throws IOException {
        buffer.rewind();
        final int written = channel.write(buffer);
        if (written != buffer.capacity()) {
            throw new IOException(String.format(
                    "Attempted to write %s bytes, but could only write %s!",
                    buffer.capacity(),
                    written
            ));
        }
    }

    public void writeByte(final byte b) throws IOException {
        write1ByteSigned(b);
    }

    public void writeBytes(final byte[] b) throws IOException {
        final ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.order(byteOrder);
        writeFully(buffer);
    }

    public void write1ByteSigned(final byte b) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(byteOrder);
        buffer.put(b);
        writeFully(buffer);
    }

    public void write1ByteUnsigned(final short s) throws IOException {
        write1ByteSigned((byte) (s & 0xFF));
    }

    public void write2ByteSigned(final short s) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        buffer.putShort(s);
        writeFully(buffer);
    }

    public void write2ByteUnsigned(final int i) throws IOException {
        write2ByteSigned((short) (i & 0xFFFF));
    }

    public void write3ByteSigned(final int i) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(3);
        buffer.order(byteOrder);
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            buffer.put((byte) ((i >> 16) & 0xFF));
            buffer.put((byte) ((i >> 8) & 0xFF));
            buffer.put((byte) (i & 0xFF));
        } else { // LITTLE_ENDIAN
            buffer.put((byte) (i & 0xFF));
            buffer.put((byte) ((i >> 8) & 0xFF));
            buffer.put((byte) ((i >> 16) & 0xFF));
        }
        writeFully(buffer);
    }

    public void write3ByteUnsigned(final int i) throws IOException {
        write3ByteSigned(i);
    }

    public void write4ByteSigned(final int i) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(byteOrder);
        buffer.putInt(i);
        writeFully(buffer);
    }

    public void write4ByteUnsigned(final long l) throws IOException {
        write4ByteSigned((int) l);
    }

    public void write8ByteSigned(final long l) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(byteOrder);
        buffer.putLong(l);
        writeFully(buffer);
    }

    public void writeFloat(final float f) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.order(byteOrder);
        buffer.putFloat(f);
        writeFully(buffer);
    }

    public void writeDouble(final double d) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.order(byteOrder);
        buffer.putDouble(d);
        writeFully(buffer);
    }

    public void writeChars(final char[] chars) throws IOException {
        writeFully(charset.encode(CharBuffer.wrap(chars)));
    }

    public void flush() throws IOException {
        lowerLayer.flush();
    }
}
