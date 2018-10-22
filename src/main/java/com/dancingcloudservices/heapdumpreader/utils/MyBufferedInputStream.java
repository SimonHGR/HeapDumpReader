package com.dancingcloudservices.heapdumpreader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class MyBufferedInputStream extends InputStream {
    private final int BUF_SIZE;
    private final int BUF_COUNT = 2;
    private final InputStream source;
    private final byte[][] buffs;
    // this buffer contains live data from progress onward, the other is old
    // initialization will revert it to zero!
    private int bufInUse = 1;
    private int next = 0;
    private int thisBufferHighWater;
    private boolean closed = false;
    private int buffersFullCount = 0; // for debug

    public MyBufferedInputStream(InputStream source, int bufferSize) throws IOException {
        BUF_SIZE = bufferSize;
        this.source = source;
        buffs = new byte[BUF_COUNT][BUF_SIZE];
        prepareNextBuffer();
    }

    public MyBufferedInputStream(InputStream source) throws IOException {
        this(source, 4096);
    }

    // don't call this until the buffer currently in use is empty
    public void prepareNextBuffer() throws IOException {
        int nextBuffer = (bufInUse + 1) % BUF_COUNT;
        byte[] buffer = buffs[nextBuffer];

        thisBufferHighWater = source.read(buffer, 0, BUF_SIZE);
        bufInUse = nextBuffer; // now using the new one
        next = 0;
        buffersFullCount++;
    }

    @Override
    public int read(byte[] cbuf, int off, int lenToCopy) throws IOException {
        if (closed) throw new IllegalStateException("Already closed");
        int copiedSoFar = 0;
        while (lenToCopy > 0 && thisBufferHighWater > 0) {
            int inBuffer = thisBufferHighWater - next;
            if (inBuffer > 0) {
                if (lenToCopy <= inBuffer) {
                    System.arraycopy(buffs[bufInUse], next, cbuf, off, lenToCopy);
                    next += lenToCopy;
                    copiedSoFar += lenToCopy;
                    lenToCopy = 0;
                } else {
                    System.arraycopy(buffs[bufInUse], next, cbuf, off, inBuffer);
                    off += inBuffer;
                    copiedSoFar += inBuffer;
                    lenToCopy -= inBuffer;
                    // go for next buffer-full (might return zero if we're already at source-eof
                    prepareNextBuffer();
                }
            } else {
                prepareNextBuffer();
                inBuffer = this.thisBufferHighWater;
            }
        }
        return copiedSoFar;
    }

    @Override
    public int read(byte[] buff) throws IOException {
        return read(buff, 0, buff.length);
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int count = read(b);
        if (count == 1) return b[0];
        else return -1;
    }

    @Override
    public int available() throws IOException {
        int inBuffer = thisBufferHighWater - next;
        if (inBuffer > 0) return inBuffer;
        else return source.available();
    }

    @Override
    public void close() throws IOException {
        source.close();
        closed = true;
    }
}
