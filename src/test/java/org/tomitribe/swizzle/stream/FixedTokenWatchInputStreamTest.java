/**
 *
 * Copyright 2018 Tomitribe
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

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FixedTokenWatchInputStreamTest extends TestCase {

    public void testTokenizingFilterInputStream() throws Exception {
        swizzleAndAssert("FOO",
                input("FOO"),
                expected("FOO")
        );

        swizzleAndAssert("FOO",
                input("FFOO"),
                expected("FOO")
        );

        swizzleAndAssert("F",
                input("FFOO"),
                expected("F", "F")
        );

        swizzleAndAssert("F",
                input("FFOFO"),
                expected("F","F", "F")
        );

        swizzleAndAssert("FO",
                input("FFOFO"),
                expected("FO","FO")
        );

    }

    private String input(final String... strings) {
        return Join.join("", strings);
    }

    private String[] expected(final String... strings) {
        return strings;
    }

    private void swizzleAndAssert(String token, String original, String... expected) throws IOException {

        {
            final List<String> actual = new ArrayList<>();
            InputStream in = StreamUtils.stringToStream(original);
            in = new FixedTokenWatchInputStream(in, token, actual::add);
            final String completed = StreamUtils.streamToString(in);

            assertEquals(Join.join("\n", expected), Join.join("\n", actual));
            assertEquals(original, completed);
        }
        {
            final List<String> actual = new ArrayList<>();
            InputStream in = StreamUtils.stringToStream(original);
            in = new FixedTokenWatchInputStream(in, token, actual::add);
            final String completed = streamToStringChunked(in);

            assertEquals(Join.join("\n", expected), Join.join("\n", actual));
            assertEquals(original, completed);
        }
    }

    private String streamToStringChunked(InputStream in) throws IOException {
        final StringBuilder text = new StringBuilder();
        final byte[] buf = new byte[256];
        try {
            int s;
            while ((s = in.read(buf)) != -1) {
                text.append(new String(buf, 0, s));
            }
        } finally {
            in.close();
        }
        return text.toString();
    }

}