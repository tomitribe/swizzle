/**
 *
 * Copyright 2003 David Blevins
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

import java.io.IOException;
import java.io.InputStream;

public class IncludeFilterInputStream extends FilteredInputStream {

    private final ScanBuffer beginBuffer;
    private final ScanBuffer endBuffer;
    private State state;
    private boolean keepDelimiters;

    public IncludeFilterInputStream(InputStream in, String begin, String end) {
        this(in, begin, end, true);
    }

    public IncludeFilterInputStream(InputStream in, String begin, String end, boolean caseSensitive) {
        this(in, begin, end, caseSensitive, true);
    }

    public IncludeFilterInputStream(InputStream in, String begin, String end, boolean caseSensitive, final boolean keepDelimiters) {
        super(in);

        beginBuffer = new ScanBuffer(begin.length());
        endBuffer = new ScanBuffer(end.length());

        beginBuffer.setScanString(begin, caseSensitive);
        endBuffer.setScanString(end, caseSensitive);

        this.keepDelimiters = keepDelimiters;

        if (keepDelimiters) {
            state = findBegin;
        } else {
            state = findEnd;
        }

    }

    public int read() throws IOException {
        return state.read();
    }

    private int this$read() throws IOException {
        return this.read();
    }

    private int super$read() throws IOException {
        return super.read();
    }

    private final State findBegin = new State() {
        @Override
        public int read() throws IOException {
            int b = super$read();

            while (b != -1) {
                beginBuffer.append(b);
                if (beginBuffer.match()) {

                    if (keepDelimiters) {
                        state = flushBeginToken;
                    } else {
                        state = findEnd;
                    }

                    break;
                } else {
                    b = super$read();
                }
            }
            b = (b == -1) ? b : state.read();
            return b;
        }
    };

    private final State flushBeginToken = new State() {
        @Override
        public int read() throws IOException {
            if (!keepDelimiters) beginBuffer.flush();
            final int flushed = beginBuffer.append(-1);

            if (keepDelimiters && flushed != -1) {

                return flushed;

            } else {

                state = findEnd;
                return state.read();

            }
        }
    };

    private final State findEnd = new State() {
        @Override
        public int read() throws IOException {
            int b, a = b = super$read();

            // Look for the END token.
            // If the end token is not found.
            // Let the byte go.
            b = endBuffer.append(b);
            if (endBuffer.match()) {
                if (keepDelimiters) {
                    state = flushEndToken;
                } else {
                    endBuffer.flush();
                    state = findBegin;
                }
            }
            b = (b == -1 && a != -1) ? state.read() : b;
            return b;
        }
    };

    private final State flushEndToken = new State() {
        @Override
        public int read() throws IOException {
            if (!keepDelimiters) endBuffer.flush();

            final int flushed = endBuffer.append(-1);

            if (flushed != -1) {

                return flushed;

            } else {

                state = findBegin;
                return state.read();

            }
        }
    };


    public static interface State {
        public abstract int read() throws IOException;
    }
}
