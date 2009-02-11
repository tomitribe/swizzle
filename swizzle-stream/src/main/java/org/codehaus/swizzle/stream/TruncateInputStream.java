/**
 *
 * Copyright 2006 David Blevins
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
import java.io.ByteArrayInputStream;

/**
 * Reads from the underlying stream up until the token passed in.
 * After the token passed in is read, only -1 will be returned without
 * affecting the underlying stream.
 * 
 * @version $Revision$ $Date$
 */
public class TruncateInputStream extends FilterInputStream {

    private final ScanBuffer tokenBuffer;

    public TruncateInputStream(InputStream in, String end) {
        this(in, end, false);
    }
    
    public TruncateInputStream(InputStream in, String end, boolean caseSensitive) {
        super(in);

        tokenBuffer = new ScanBuffer(end, caseSensitive);
        strategy = lookingForToken;
    }

    private StreamReadingStrategy strategy;

    public int read() throws IOException {
        return strategy._read();
    }

    interface StreamReadingStrategy {
        int _read() throws IOException;
    }

    private final StreamReadingStrategy flushingBuffer = new StreamReadingStrategy() {
        public int _read() throws IOException {
            return tokenBuffer.append(-1);
        }
    };

    private final StreamReadingStrategy lookingForToken = new StreamReadingStrategy() {
        public int _read() throws IOException {
            int stream = superRead();
            int buffer = tokenBuffer.append(stream);

            if (tokenBuffer.match()) {

                strategy = flushingBuffer;

                int i = (buffer == -1 && stream != -1) ? read() : buffer;
                return i;
            }

            // Have we just started?

            // The buffer starts out in -1 state. If the
            // data coming from the stream is valid, we
            // need to just keep reading till the buffer
            // gives us good data.
            int i = (buffer == -1 && tokenBuffer.hasData()) ? _read() : buffer;
            return i;
        }
    };

    private int superRead() throws IOException {
        return super.read();
    }
}
