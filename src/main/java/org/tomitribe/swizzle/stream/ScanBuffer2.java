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

    private final Value value;
    private final String token;
    private final boolean caseSensitive;

    public ScanBuffer2(final String token, final boolean caseSensitive) {
//        this.token = new Buffer(caseSensitive ? token.getBytes() : token.toLowerCase().getBytes());
        this.token = caseSensitive ? token : token.toLowerCase();
        this.value = new Value(token.length());

        this.caseSensitive = caseSensitive;
    }

    public int append(int newByte) {
        return value.add(newByte);
    }

    private boolean matches(int newByte, int expected) {
        if (caseSensitive) return expected == newByte;
        return expected == Character.toLowerCase(newByte);
    }

    public int drain() {
        return value.drain();
    }

    public boolean matches() {
        if (caseSensitive) return token.equals(value.toString());
        else return token.equalsIgnoreCase(value.toString());
    }

    public int available() {
        return value.length();
    }

    public boolean matching() {
        if (caseSensitive) return token.startsWith(value.toString());
        else return token.startsWith(value.toString().toLowerCase());
    }

    public String asString() {
        return value.toString();
    }

    public void reset() {
        value.reset();
    }

    /**
     * The index in a buffer wraps around to the beginning
     * once it reaches the end.  The Index class allows that
     * logic to be enforced without complicating the referring
     * code.
     */
    public static class Index {
        private final int max;
        private int position;

        public Index(int max) {
            this.max = max;
        }

        public int get() {
            return position;
        }

        public int increment() {
            position++;
            if (position == max) position = 0;
            return position;
        }

        public void reset() {
            position = 0;
        }
    }

    /**
     * Value wraps a Buffer and as data is written
     * will track where in the buffer the data starts
     * stops and its length.
     */
    public static class Value {
        private final Buffer buffer;
        private final Length length;
        private final Index start;

        public Value(final int max) {
            this.buffer = new Buffer(new byte[max]);
            this.start = new Index(max);
            this.length = new Length(max);
            buffer.fill(-1);
        }

        public int length() {
            return length.get();
        }

        public int add(final int value) {
            if (length.get() == 0 && value == -1) return -1;

            final int removed = buffer.set(value);
            buffer.advance();

            if (value == -1) length.decrement();
            else length.increment();

            if (removed != -1) start.increment();

            if (length.get() == 0) {
                buffer.reset();
                start.reset();
            }

            return removed;
        }

        /**
         * Trims and returns the value from the front of the buffer.
         */
        public int drain() {
            if (length.get() == 0) return -1;

            final int removed = buffer.set(start.get(), -1);

            /*
             * If we have no data, set our indexes to zero
             * as this is faster when creating strings from
             * the buffer since no array copying is needed
             */
            if (length.decrement() == 0) {
                start.reset();
                buffer.reset();
            } else {
                start.increment();
            }
            return removed;
        }

        public String toString() {
            final int start = this.start.get();
            final int length = this.length.get();

            if (start + length <= buffer.length()) {
                return new String(buffer.buffer, start, length);
            }

            final byte[] bytes = new byte[length];

            final int lengthToEnd = buffer.length() - start;
            System.arraycopy(buffer.buffer, start, bytes, 0, lengthToEnd);
            System.arraycopy(buffer.buffer, 0, bytes, lengthToEnd, length - lengthToEnd);

            return new String(bytes);
        }

        public void reset() {
            start.reset();
            buffer.reset();
            length.reset();
        }
    }

    /**
     * Counts the bytes written to a buffer, never exceeding
     * the length of the buffer and never dropping below zero
     */
    public static class Length {
        private final int max;
        private int length;

        public Length(final int max) {
            this.max = max;
        }

        public int get() {
            return length;
        }

        public void increment() {
            length = Math.min(length + 1, max);
        }

        public int decrement() {
            length = Math.max(length - 1, 0);
            return length;
        }

        public void reset() {
            length = 0;
        }
    }

    public static class Buffer {
        private final Index index;
        private final byte[] buffer;

        public Buffer(final byte[] buffer) {
            this.buffer = buffer;
            this.index = new Index(buffer.length);
        }

        public int length() {
            return buffer.length;
        }

        public int get() {
            return buffer[index.get()];
        }

        public int set(final int b) {
            final int i = index.get();
            return set(i, b);
        }

        public int set(int index, int b) {
            final byte oldByte = buffer[index];
            buffer[index] = (byte) b;
            return oldByte;
        }

        public void advance() {
            index.increment();
        }

        public void reset() {
            index.reset();
            fill(-1);
        }

        public int index() {
            return index.get();
        }

        @Override
        public String toString() {
            return "Buffer{" +
                    "index=" + index +
                    ", buffer='" + new String(buffer) +
                    "'}";
        }

        public void fill(int i) {
            Arrays.fill(buffer, (byte) i);
        }
    }
}
