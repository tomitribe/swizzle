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

public abstract class FilteredInputStream extends FilterInputStream {

    private boolean done = false;

    public FilteredInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {

        int count = 0;

        if (done) {
            return (-1);
        }

        for (int i = off, max = off + len; i < max; i++) {

            final int read = read();

            if (read == -1) {
                done = true;
                return count == 0 ? -1 : count;
            }

            bytes[i] = (byte) read;
            count++;
        }

        return count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
