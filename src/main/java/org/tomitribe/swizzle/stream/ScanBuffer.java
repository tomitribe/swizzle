/**
 *
 * Copyright 2001 David Blevins
 *
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
package org.tomitribe.swizzle.stream;

public class ScanBuffer {

    private char[] buffer = new char[0];
    private int[] buffer2 = new int[0];
    private char[] token = new char[0];
    private int pos;

    boolean cs;

    public ScanBuffer(String scanString) {
        this(scanString, false);
    }

    public ScanBuffer(int size) {
        buffer = new char[size];
        buffer2 = new int[size];
        token = new char[size];
        flush();
        cs = true;
    }

    public ScanBuffer(String scanString, boolean caseSensitive) {
        this(scanString.length());
        setScanString(scanString, caseSensitive);
    }

    public int size() {
        return buffer.length;
    }

    public String toString() {
        return getClass().getSimpleName() + "#" + getScanString() + " # buffer[" + new String(getBuffer()) + "]";
    }

    public void resetPosition() {
        pos = 0;
    }

    public String getScanString() {
        return new String(token);
    }

    public void setScanString(String stringToken) {
        setScanString(stringToken, true);
    }

    public void setScanString(String stringToken, boolean caseSensitive) {
        cs = caseSensitive;
        // Optimize - no need to recreate char array everytime
        token = new char[stringToken.length()];
        stringToken.getChars(0, token.length, token, 0);

        if (token.length > buffer.length) {
            buffer = new char[token.length * 4];
            buffer2 = new int[token.length * 4];
        }

        pos = 0;
        if (!cs) for (int i = 0; i < token.length; i++)
            token[i] = Character.toLowerCase(token[i]);

        flush();
    }

    public int append(int newByte) {
        if (token.length == 0) return newByte;
        int old = buffer2[pos];

        buffer2[pos] = newByte;
        buffer[pos] = (cs) ? (char) newByte : Character.toLowerCase((char) newByte);

        pos = (++pos < buffer.length) ? pos : 0;
        return old;
    }

    public void flush() {
        char NULL = (char) -1;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = NULL;
            buffer2[i] = -1;
        }
        resetPosition();
    }

    public boolean match() {
        if (size() == 0) return true;

        // Absolute position (apos) is the number of characters actually
        // searched.
        // This number should never exceed the length of the token.
        int apos = token.length - 1;

        // The relative position indicates where we are on the buffer.
        // It is possible to reach the end of the buffer before we are
        // finished searching enough characters. In this contition we
        // wrap to the beginning of the buffer and continue matching
        // the remaining chars of the token array against the buffer.
        int rpos = pos - 1;

        // We start the search, possibly from the middle of the buffer,
        // and go as far as we can. We will either come to the end of the
        // token or the end of the buffer. If we come to the end of the
        // buffer the next for loop wil naturally pick up at the beginning
        // of the buffer and continue matching where we left off in the
        // token array.
        for (; rpos > -1 && apos > -1; rpos--, apos--) {
            if (buffer[rpos] != token[apos]) return false;
        }
        for (rpos = buffer.length - 1; apos > -1; rpos--, apos--) {
            if (buffer[rpos] != token[apos]) return false;
        }
        // for (; rpos < buffer.length && apos < token.length; rpos++, apos++) {
        // if (buffer[rpos] != token[apos]) return false;
        // }
        // for (rpos = 0; apos < token.length; rpos++, apos++ ) {
        // if (buffer[rpos] != token[apos]) return false;
        // }

        return true;
    }

    public boolean hasData() {
        int apos = token.length - 1;
        int rpos = pos - 1;
        for (; rpos > -1 && apos > -1; rpos--, apos--) {
            if (buffer2[rpos] != -1) return true;
        }
        for (rpos = buffer2.length - 1; apos > -1; rpos--, apos--) {
            if (buffer2[rpos] != -1) return true;
        }
        return false;
    }

    private void log(String str) {
        System.out.println("[Scan] " + str);
    }

    public void clear(int i) {
        char NULL = (char) -1;

        // Absolute position (apos) is the number of characters actually
        // searched.
        // This number should never exceed the length of the token.
        int apos = i - 1;

        // The relative position indicates where we are on the buffer.
        // It is possible to reach the end of the buffer before we are
        // finished searching enough characters. In this contition we
        // wrap to the beginning of the buffer and continue matching
        // the remaining chars of the token array against the buffer.
        int rpos = pos - 1;

        // We start the search, possibly from the middle of the buffer,
        // and go as far as we can. We will either come to the end of the
        // token or the end of the buffer. If we come to the end of the
        // buffer the next for loop wil naturally pick up at the beginning
        // of the buffer and continue matching where we left off in the
        // token array.
        for (; rpos > -1 && apos > -1; rpos--, apos--) {
            buffer[rpos] = NULL;
            buffer2[rpos] = -1;
        }
        for (rpos = buffer.length - 1; apos > -1; rpos--, apos--) {
            buffer[rpos] = NULL;
            buffer2[rpos] = -1;
        }
    }

    public byte[] getBuffer() {
        byte[] out = new byte[getSize()];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) getByte(buffer.length - out.length + i);
        }
        return out;
    }

    /**
     * The current size of the buffer. The number of non -1 items in the buffer.
     */
    private int getSize() {
        int size = 0;
        for (int i = buffer.length - 1; i >= 0; i--) {
            int b = getByte(i);
            if (b != -1) {
                size++;
            } else {
                break;
            }
        }
        return size;
    }

    /**
     * Gets the byte at the specified absolute positon
     */
    private int getByte(int absolutePosition) {
        if (absolutePosition >= buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        int realPosition = (pos + absolutePosition) % buffer.length;
        return buffer2[realPosition];
    }
}
