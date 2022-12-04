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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @version $Revision$ $Date$
 */
public class StreamUtils {
    public static String streamToString(InputStream in) throws IOException {
        StringBuffer text = new StringBuffer();
        try {
            int b;
            while ((b = in.read()) != -1) {
                text.append((char) b);
            }
        } finally {
            in.close();
        }
        return text.toString();
    }

    public static InputStream stringToStream(String original) {
        return new ByteArrayInputStream(original.getBytes());
    }

    public static byte[] join(byte[]... buffers) {
        // calculate the final size
        int size = 0;
        for (byte[] buffer : buffers) {
            size += buffer.length;
        }

        // creat the output bufffer
        byte[] out = new byte[size];

        // copy each buffer into the output buffer
        int off = 0;
        for (byte[] buffer : buffers) {
            System.arraycopy(buffer, 0, out, off, buffer.length);
            off += buffer.length;
        }
        return out;
    }
}
