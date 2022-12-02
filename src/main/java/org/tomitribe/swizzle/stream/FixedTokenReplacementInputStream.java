/**
 * Copyright 2003 David Blevins
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

import java.io.IOException;
import java.io.InputStream;

public class FixedTokenReplacementInputStream extends FilteredInputStream {

    private final ScanBuffer tokenBuffer;
    private final StreamTokenHandler handler;
    private InputStream value;
    private String token;
    private byte firstByte;

    public FixedTokenReplacementInputStream(InputStream in, String token, StreamTokenHandler handler) {
        this(in, token, handler, true);
    }

    public FixedTokenReplacementInputStream(InputStream in, String token, StreamTokenHandler handler, boolean caseSensitive) {
        super(in);
        this.tokenBuffer = new ScanBuffer(token, caseSensitive);
        this.handler = handler;
        this.strategy = lookingForToken;
        this.token = token;
        this.firstByte = token.getBytes()[0];
    }

    private StreamReadingStrategy strategy;

    public int read() throws IOException {
        return strategy._read();
    }

    // reading url (looking for end)
    // flushing url
    // regular read (looking for begin)
    interface StreamReadingStrategy {
        int _read() throws IOException;
    }

    private final StreamReadingStrategy flushingValue = new StreamReadingStrategy() {
        public int _read() throws IOException {
            int i = value.read();
            if (i == -1) {
                strategy = lookingForToken;
                i = read();
            }
            return i;
        }
    };

    private final StreamReadingStrategy lookingForToken = new StreamReadingStrategy() {
        public int _read() throws IOException {
            int stream = superRead();

            if (stream == -1) {
                strategy = done;
                return -1;
            }

            /*
             * Have we potentially found the start of our token?
             */
            if (stream != firstByte) {
                return stream;
            }

            strategy = readingToken;

            int buffer = tokenBuffer.append(stream);

            if (tokenBuffer.match()) {
                tokenBuffer.flush();

                String token = tokenBuffer.getScanString();
                value = handler.processToken(token);
                strategy = flushingValue;

                int i = (buffer == -1 && stream != -1) ? read() : buffer;
                return i;
            }

            // Have we just started?

            // The buffer starts out in -1 state. If the
            // data coming from the stream is valid, we
            // need to just keep reading till the buffer
            // gives us good data.
            int i = (buffer == -1 && tokenBuffer.hasData()) ? read() : buffer;
            return i;
        }
    };

    private final StreamReadingStrategy readingToken = new StreamReadingStrategy() {
        public int _read() throws IOException {
            while (true) {
                int stream = superRead();
                int buffer = tokenBuffer.append(stream);

                if (tokenBuffer.match()) {
                    tokenBuffer.flush();

                    String token = tokenBuffer.getScanString();
                    value = handler.processToken(token);
                    strategy = flushingValue;

                    int i = (buffer == -1 && stream != -1) ? read() : buffer;
                    return i;
                }

                // Have we just started?

                // The buffer starts out in -1 state. If the
                // data coming from the stream is valid, we
                // need to just keep reading till the buffer
                // gives us good data.
                if (buffer == -1) {
                    if (tokenBuffer.hasData()) {
                        continue;
                    } else {
                        strategy = done;
                        return -1;
                    }
                }
                return buffer;
            }
        }
    };

    private final StreamReadingStrategy done = new StreamReadingStrategy() {
        @Override
        public int _read() throws IOException {
            return -1;
        }
    };


    private int superRead() throws IOException {
        return super.read();
    }
}
