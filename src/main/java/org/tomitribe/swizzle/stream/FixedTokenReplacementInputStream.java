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

    private final ScanBuffer2 buffer;
    private final StreamTokenHandler handler;
    private String token;
    private byte firstByte;

    public FixedTokenReplacementInputStream(final InputStream in, final String token, final StreamTokenHandler handler) {
        this(in, token, handler, true);
    }

    public FixedTokenReplacementInputStream(InputStream in, String token, StreamTokenHandler handler, boolean caseSensitive) {
        super(in);
        this.buffer = new ScanBuffer2(token, caseSensitive);
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

    private final StreamReadingStrategy flushingBuffer = new FlushingBuffer();
    private final StreamReadingStrategy lookingForToken = new LookingForToken();
    private final StreamReadingStrategy readingToken = new ReadingToken();
    private final StreamReadingStrategy done = new Done();


    private int superRead() throws IOException {
        final int read = super.read();
        return read;
    }

    private static class Done implements StreamReadingStrategy {
        @Override
        public int _read() throws IOException {
            return -1;
        }
    }

    private class FlushingBuffer implements StreamReadingStrategy {
        @Override
        public int _read() throws IOException {
            if (buffer.available() > 0) {
                final int drained = buffer.drain();

                if (buffer.matching()) {
                    strategy = readingToken;
                }

                return drained;
            }

            strategy = lookingForToken;
            return read();
        }
    }

    private class FlushingToken implements StreamReadingStrategy {

        private final InputStream inputStream;

        public FlushingToken(final InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public int _read() throws IOException {
            int i = inputStream.read();
            if (i != -1) return i;

            strategy = lookingForToken;
            return read();
        }
    }

    private class ReadingToken implements StreamReadingStrategy {
        public int _read() throws IOException {
            while (true) {
                int stream = superRead();
                int old = buffer.append(stream);

                if (buffer.matches()) {
                    buffer.reset();
                    strategy = new FlushingToken(handler.processToken(token));

                    int i = (old == -1 && stream != -1) ? read() : old;
                    return i;
                }

                if (!buffer.matching()) {
                    strategy = flushingBuffer;
                    int i = (old == -1 && stream != -1) ? read() : old;
                    return i;
                }

                // Have we just started?

                // The buffer starts out in -1 state. If the
                // data coming from the stream is valid, we
                // need to just keep reading till the buffer
                // gives us good data.
                if (old == -1) {
                    if (buffer.available() > 0) {
                        continue;
                    } else {
                        strategy = done;
                        return -1;
                    }
                }
                return old;
            }
        }
    }

    private class LookingForToken implements StreamReadingStrategy {
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

            int old = buffer.append(stream);

            if (buffer.matches()) {
                strategy = new FlushingToken(handler.processToken(token));

                int i = (old == -1) ? read() : old;
                return i;
            }

            // Have we just started?

            // The buffer starts out in -1 state. If the
            // data coming from the stream is valid, we
            // need to just keep reading till the buffer
            // gives us good data.
            int i = (old == -1 && buffer.available() > 0) ? read() : old;
            return i;
        }
    }
}
