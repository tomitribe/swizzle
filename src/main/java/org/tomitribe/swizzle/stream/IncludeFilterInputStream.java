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
    protected State state;
    private boolean keepDelimiters;

    public IncludeFilterInputStream(InputStream in, String begin, String end) {
        this(in, begin, end, true);
    }

    public IncludeFilterInputStream(InputStream in, String begin, String end, boolean caseSensitive) {
        this(in, begin, end, caseSensitive, true);
    }

    public IncludeFilterInputStream(InputStream in, String begin, String end, boolean caseSensitive, final boolean keepDelimiters) {
        super(in);

        this.beginBuffer = new ScanBuffer(begin, caseSensitive);
        this.endBuffer = new ScanBuffer(end, caseSensitive);
        this.keepDelimiters = keepDelimiters;
        this.state = findBegin;
    }

    public int read() throws IOException {
        return state.read();
    }

    private int super$read() throws IOException {
        return super.read();
    }

    protected final State findBegin = new State() {
        @Override
        public int read() throws IOException {
            int b;
            while ((b = super$read()) != -1) {
                beginBuffer.append(b);
                if (beginBuffer.match()) {

                    if (keepDelimiters) {
                        state = writeBeginBuffer;
                    } else {
                        state = findEnd;
                    }

                    return state.read();
                }
            }

            return -1;
        }
    };

    protected final State writeBeginBuffer = new State() {
        @Override
        public int read() throws IOException {
            final int b = beginBuffer.append(-1);

            if (b != -1) return b;

            state = findEnd;
            return state.read();
        }
    };

    protected final State findEnd = new State() {
        @Override
        public int read() throws IOException {

            // read till we've buffered enough to check for a match
            int buffered;
            while (true) {
                final int streamed = super$read();
                buffered = endBuffer.append(streamed);

                if (buffered != -1) break;
                if (streamed == -1) return -1; // end of stream
            }

            if (endBuffer.match()) {

                if (keepDelimiters) {

                    state = writeEndBuffer;

                } else {

                    endBuffer.flush();
                    state = findBegin;
                }
            }

            return buffered;
        }
    };

    protected final State writeEndBuffer = new State() {
        @Override
        public int read() throws IOException {
            final int b = endBuffer.append(-1);

            if (b != -1) return b;

            state = findBegin;
            return state.read();
        }
    };
}
