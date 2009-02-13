/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.codehaus.swizzle.stream;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.InputStream;
import java.io.IOException;

public class PushbackInputStream extends InputStream implements PushbackBuffer {
    private final InputStream delegate;
    private final LinkedList<PushbackBuffer> pushbackBuffers = new LinkedList<PushbackBuffer>();

    private byte[] markBuffer;
    private int markCount;

    public PushbackInputStream() {
        this(null);
    }

    public PushbackInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    public InputStream getDelegate() {
        return delegate;
    }

    public int read() throws IOException {
        int next = -1;

        // first check the pushback buffer
        for (Iterator<PushbackBuffer> iterator = pushbackBuffers.iterator(); iterator.hasNext();) {
            PushbackBuffer buffer = iterator.next();
            if (buffer.hasNext()) {
                next = buffer.next();
                break;
            }
            iterator.remove();
        }

        // if we didn't get a byte from the pushback buffer, get the next byte internally
        if (next == -1) {
            next = getNextByte();
        }

        // before returning the byte, add it to the mark buffer
        if (next != -1) {
            addToMarkBuffer((byte) next);
        }

        return next;
    }

    protected int getNextByte() throws IOException {
        if (delegate != null) {
            return delegate.read();
        }
        return -1;
    }

    /**
     * Add a byte to the mark buffer if mark is active
     */
    protected void addToMarkBuffer(byte b) {
        // if there is no mark we are done
        if (markBuffer == null) {
            return;
        }

        // growBuffer if necessary
        if (markCount >= markBuffer.length) {
            byte[] oldBuf = markBuffer;
            markBuffer = new byte[oldBuf.length * 2];

            System.arraycopy(oldBuf, 0, markBuffer, 0, oldBuf.length);
        }

        // add byte
        markBuffer[markCount++] = b;
    }

    public boolean markSupported() {
        return true;
    }

    /**
     * Activate mark/reset buffer.
     * This will release any previous marks.
     * @param readlimit suggested mark buffer size
     */
    public void mark(int readlimit) {
        markBuffer = new byte[readlimit];
    }

    /**
     * Release current mark/reset buffer.
     */
    public void unmark() {
        markBuffer = null;
        markCount = 0;
    }

    /**
     * Push the mark/reset buffer onto the pushback buffer, and release the mark
     */
    public void reset() {
        if (markCount > 0) {
            pushbackBuffers.add(new PushbackBuffer(markBuffer, 0, markCount));
        }
        unmark();
    }

    public void unread(byte[] bytes) {
        unread(bytes, 0, bytes.length);
    }

    public void unread(byte[] bytes, int off, int len) {
        pushbackBuffers.add(new PushbackBuffer(bytes, off, len));
    }

    public byte[] getBuffer() {
        int size = 0;
        for (PushbackBuffer buffer : pushbackBuffers) {
            size += buffer.size();
        }

        int off = 0;
        byte[] out = new byte[size];
        for (PushbackBuffer buffer : pushbackBuffers) {
            off += buffer.copyBuffer(out, off);
        }
        return out;
    }

    private static class PushbackBuffer {
        private final byte[] buf;
        private int pos;
        private final int end;

        private PushbackBuffer(byte[] buf, int off, int len) {
            if (buf == null) throw new NullPointerException("buf is null");
            if (off < 0) throw new IllegalArgumentException("pos is negative");
            if (len < 0) throw new IllegalArgumentException("len is negative");
            if (off + len > buf.length) throw new IllegalArgumentException("off + len is greater then buf size");
            this.buf = buf;
            this.pos = off;
            this.end = off + len;
        }

        public boolean hasNext() {
            return pos < end;
        }

        public byte next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return buf[pos++];
        }

        public int size() {
            return Math.max(end - pos, 0);
        }

        public int copyBuffer(byte[] outBuf, int off) {
            int size = size();
            System.arraycopy(buf, pos, outBuf, off, size);
            return size;
        }
    }
}