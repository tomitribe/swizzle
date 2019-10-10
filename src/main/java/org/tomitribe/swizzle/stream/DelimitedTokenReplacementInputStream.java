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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelimitedTokenReplacementInputStream extends FilteredInputStream {

    private final ScanBuffer beginBuffer;
    private final ScanBuffer endBuffer;
    private InputStream value;
    private final StreamTokenHandler handler;

    public DelimitedTokenReplacementInputStream(InputStream in, String begin, String end, StreamTokenHandler tokenHandler) {
        this(in, begin, end, tokenHandler, true);
    }

    public DelimitedTokenReplacementInputStream(InputStream in, String begin, String end, StreamTokenHandler tokenHandler, boolean caseSensitive) {
        super(in);
        this.handler = tokenHandler;

        beginBuffer = new ScanBuffer(begin, caseSensitive);
        endBuffer = new ScanBuffer(end, caseSensitive);

        strategy = fillBeginBuffer;
    }

    private DelimitedTokenReplacementInputStream.StreamReadingStrategy strategy;

    public int read() throws IOException {
        return strategy._read();
    }

    // reading url (looking for end)
    // flushing url
    // regular read (looking for begin)
    interface StreamReadingStrategy {
        int _read() throws IOException;
    }

    private final DelimitedTokenReplacementInputStream.StreamReadingStrategy readingToken = new DelimitedTokenReplacementInputStream.StreamReadingStrategy() {
        public int _read() throws IOException {
            endBuffer.flush();
            StringBuffer token = new StringBuffer();

            while (true) {
                int stream = superRead();
                int buffer = endBuffer.append(stream);
                char s = (char) stream, b = (char) buffer;

                if (buffer == -1 && stream != -1) {
                    // Have we just started?
                    continue;
                } else if (buffer == -1 && stream == -1) {
                    token.insert(0, beginBuffer.getScanString());
                    value = new ByteArrayInputStream(token.toString().getBytes());
                    endBuffer.resetPosition();
                    strategy = flushingValue;
                    return strategy._read();
                }

                token.append((char) buffer);

                if (endBuffer.match()) {
                    break;
                }
            }

            value = handler.processToken(token.toString());
            strategy = flushingValue;

            return strategy._read();
        }
    };

    private final DelimitedTokenReplacementInputStream.StreamReadingStrategy flushingValue = new DelimitedTokenReplacementInputStream.StreamReadingStrategy() {
        public int _read() throws IOException {
            // todo is this correct?
            if (value == null) {
                return -1;
            }
            int i = value.read();
            if (i == -1) {
                strategy = fillBeginBuffer;
                i = strategy._read();
            }
            return i;
        }
    };

    private final DelimitedTokenReplacementInputStream.StreamReadingStrategy fillBeginBuffer = new DelimitedTokenReplacementInputStream.StreamReadingStrategy() {
        public int _read() throws IOException {

            if (beginBuffer.size() == 0) {
                strategy = readingToken;
                return strategy._read();
            }

            // Reset the buffer
            beginBuffer.flush();

            // Fill up the begin buffer
            for (int i = 0; i < beginBuffer.size(); i++) {
                int stream = superRead();
                beginBuffer.append(stream);
            }

            if (beginBuffer.match()) {
                beginBuffer.flush();
                strategy = readingToken;
            } else {
                strategy = primedBeginBuffer;
            }

            return strategy._read();
        }
    };

    private final DelimitedTokenReplacementInputStream.StreamReadingStrategy primedBeginBuffer = new DelimitedTokenReplacementInputStream.StreamReadingStrategy() {
        public int _read() throws IOException {

            int buffered = beginBuffer.append(superRead());

            if (beginBuffer.match()) {
                beginBuffer.flush();
                strategy = readingToken;
            }

            return buffered;
        }
    };

    private int superRead() throws IOException {
        return super.read();
    }
}
