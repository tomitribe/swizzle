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
package org.tomitribe.swizzle.stream;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * @version $Revision$ $Date$
 */
public class ReplayInputStream extends FilteredInputStream {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public ReplayInputStream(InputStream in) {
        super(in);
    }

    public int read() throws IOException {
        int i = super.read();
        if (i != -1){
            out.write(i);
        }
        return i;
    }

    public byte[] getBytesRead() {
        return out.toByteArray();
    }

    public void reset() {
        out.reset();
    }
}
