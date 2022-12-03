/**
 * Copyright 2001 David Blevins
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.swizzle.stream;

import java.util.Arrays;

public class ScanBuffer2 {

    private final byte[] buffer;
    private final boolean caseSensitive;
    private final byte[] token;

    private int bindex;
    private int length;
    private int tindex;
    private boolean matching;
    private boolean matched;

    public ScanBuffer2(final String token, final boolean caseSensitive) {
        this.token = caseSensitive ? token.getBytes() : token.toLowerCase().getBytes();
        this.buffer = new byte[token.length()];
        Arrays.fill(buffer, (byte) -1);

        this.caseSensitive = caseSensitive;
    }

    public int append(int newByte) {
        final byte old = buffer[bindex];
        final byte expected = token[tindex];

        buffer[bindex] = (byte) newByte;

        bindex = bindex + 1 < buffer.length ? bindex + 1 : 0;
        matching = (matching || length == 0) && (expected == newByte);

        length = Math.min(length + 1, buffer.length);

        matched = matching && length == token.length;

        tindex = matching ? Math.min(token.length, tindex + 1) : 0;
        tindex = matched ? 0 : tindex;


        return old;
    }

    public int drain() {
        if (length == 0) return -1;

        int start = (bindex - length) < 0 ? bindex + buffer.length - length : bindex - length;

        final byte old = buffer[start];
        buffer[start] = -1;
        length = Math.max(length - 1, 0);

        tindex = 0;
        int index = start + 1;
        matching = true;

        for (; matching && index < buffer.length && tindex < token.length && tindex < length; index++, tindex++) {
            matching = buffer[index] == token[tindex];
        }

        for (index = 0; matching && index < buffer.length && tindex < token.length && tindex < length; index++, tindex++) {
            matching = buffer[index] == token[tindex];
        }

        matched = matching && length == token.length;

        return old;
    }

    public boolean matches() {
        return matched;
    }

    public int available() {
        return length;
    }

    public boolean matching() {
        return matching;
    }

    public String asString() {
        final char[] chars = chars(buffer);
        final int start = (bindex - length) < 0 ? bindex + buffer.length - length : bindex - length;

        if (start + length <= buffer.length) {
            return new String(buffer, start, length);
        }

        final byte[] bytes = new byte[length];

        final int lengthToEnd = buffer.length - start;
        System.arraycopy(buffer, start, bytes, 0, lengthToEnd);
        System.arraycopy(buffer, 0, bytes, lengthToEnd, length - lengthToEnd);

        return new String(bytes);
    }

    private char[] chars(byte[] buffer) {
        final char[] chars = new char[buffer.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) buffer[i];
        }
        return chars;
    }


}
