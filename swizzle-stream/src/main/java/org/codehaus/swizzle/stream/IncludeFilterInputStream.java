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

public class IncludeFilterInputStream extends FilterInputStream {

    private final ScanBuffer beginBuffer;
    private final ScanBuffer endBuffer;
    protected boolean wanted;

    public IncludeFilterInputStream(InputStream in, String begin, String end) {
        this(in, begin, end, true);
    }

    public IncludeFilterInputStream(InputStream in, String begin, String end, boolean caseSensitive) {
        super(in);

        beginBuffer = new ScanBuffer(begin.length());
        endBuffer = new ScanBuffer(end.length());

        beginBuffer.setScanString(begin, caseSensitive);
        endBuffer.setScanString(end, caseSensitive);
    }

    public int read() throws IOException {

        int b, a = b = super.read();
        char c = (char) b;

        if (wanted) {
            // Look for the END token.
            // If the end token is not found.
            // Let the byte go.
            b = endBuffer.append(b);
            c = (char) b;
            if (endBuffer.match()) {
                endBuffer.flush();
                wanted = false;
            }
            b = (b == -1 && a != -1) ? read() : b;
            c = (char) b;
            return b;
        } else {
            while (!wanted && b != -1) {
                beginBuffer.append(b);
                if (beginBuffer.match()) {
                    wanted = true;
                } else {
                    b = super.read();
                    c = (char) b;
                }
            }
            b = (b == -1) ? b : read();
            c = (char) b;
            return b;
        }
    }
}
