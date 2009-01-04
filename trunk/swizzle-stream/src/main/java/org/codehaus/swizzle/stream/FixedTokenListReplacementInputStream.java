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

package org.codehaus.swizzle.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FixedTokenListReplacementInputStream extends FilterInputStream {

    private final StreamTokenHandler handler;
    private InputStream value;
    private final ScanBuffer[] tokenBuffers;
    private final ScanBuffer mainBuffer;

    public FixedTokenListReplacementInputStream(InputStream in, List tokens, StreamTokenHandler handler) {
        this(in, tokens, handler, true);
    }

    public FixedTokenListReplacementInputStream(InputStream in, List tokens, StreamTokenHandler handler,
            boolean caseSensitive) {
        super(in);
        int largestBuffer = 0;
        tokenBuffers = new ScanBuffer[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            String token = (String) tokens.get(i);
            ScanBuffer buffer = new ScanBuffer(token, caseSensitive);
            tokenBuffers[i] = buffer;
            largestBuffer = (buffer.size() > largestBuffer) ? buffer.size() : largestBuffer;
        }
        this.mainBuffer = new ScanBuffer(largestBuffer);
        this.handler = handler;
        strategy = lookingForToken;
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

    private final StreamReadingStrategy flushingMainBuffer = new StreamReadingStrategy() {
        public int _read() throws IOException {
            int buffer = mainBuffer.append(-1);

            if (buffer != -1) {
                return buffer;
            } else if (mainBuffer.hasData()) {
                return _read();
            } else {
                strategy = flushingValue;
                return read();
            }
        }
    };

    private final StreamReadingStrategy lookingForToken = new StreamReadingStrategy() {
        public int _read() throws IOException {
            int stream = superRead();
            int buffer = mainBuffer.append(stream);

            for (int i = 0; i < tokenBuffers.length; i++) {
                ScanBuffer tokenBuffer = tokenBuffers[i];
                tokenBuffer.append(stream);

                if (tokenBuffer.match()) {
                    clearAllBuffers();

                    String token = tokenBuffer.getScanString();
                    mainBuffer.clear(token.length());

                    value = handler.processToken(token);

                    if (mainBuffer.hasData()) {
                        strategy = flushingMainBuffer;
                    } else {
                        strategy = flushingValue;
                    }

                    return (buffer != -1) ? buffer : read();
                }
            }

            // Have we just started?

            // The buffer starts out in -1 state. If the
            // data coming from the stream is valid, we
            // need to just keep reading till the buffer
            // gives us good data.
            int i = (buffer == -1 && mainBuffer.hasData()) ? _read() : buffer;
            return i;
        }
    };

    private void clearAllBuffers() {
        for (int i = 0; i < tokenBuffers.length; i++) {
            ScanBuffer tokenBuffer = tokenBuffers[i];
            tokenBuffer.flush();
        }
    }

    private int superRead() throws IOException {
        return super.read();
    }
}
