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

public class DelimitedTokenWatchInputStreamTest extends TestCase {

    public void testTokenizingFilterInputStream() throws Exception {

        // Symmetrical being end pairs
        assertReplacement("{", "}");
        assertReplacement("{{", "}}");
        assertReplacement("{{{", "}}}");
        assertReplacement("{{{{", "}}}}");
        assertReplacement("{{{{{", "}}}}}");
        assertReplacement("{{{{{{{{{{{{", "}}}}}}}}}}}}");

        // Aymmetrical being end pairs
        assertReplacement("{", "}}");
        assertReplacement("{{", "}}}");
        assertReplacement("{{{", "}}}}");
        assertReplacement("{{{{", "}}}}}");
        assertReplacement("{{{{{", "}}}}}}}}}}}}");
        assertReplacement("{{", "}");
        assertReplacement("{{{", "}}");
        assertReplacement("{{{{", "}}}");
        assertReplacement("{{{{{", "}}}}");
        assertReplacement("{{{{{{{{{{{{", "}}}}}");


        assertReplacement("BEGIN", "END");

    }

    private void assertReplacement(String b, String e) throws IOException {

        swizzleAndAssert(b, e,
                input(b, "FOO", e, "abc"),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input("a" + b + "FOO" + e + "bc"),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input("ab" + b + "FOO" + e + "c"),
                expected("FOO"));

        swizzleAndAssert(b, e,
                input("abc" + b + "FOO" + e),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input("some " + b + "FOO" + e + " text"),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input(b + "FOO" + e + " text"),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input("some text " + b + "FOO" + e),
                expected("FOO")
        );

        swizzleAndAssert(b, e,
                input(b + "FOO" + e),
                expected("FOO"));

        swizzleAndAssert(b, e,
                input("some " + b + "FOO" + e + "O text"),
                expected("FOO")
        );

        swizzleAndAssert(b, e, input("some text"));

        swizzleAndAssert(b, e, input(""));

        swizzleAndAssert(b, e, input("some FO text"));

        swizzleAndAssert(b, e,
                input("some " + b + "RED" + e + " " + b + "GREEN" + e + " text"),
                expected("RED", "GREEN")
        );

        swizzleAndAssert(b, e,
                input(b + "BLUE" + e + " text " + b + "ORANGE" + e),
                expected("BLUE", "ORANGE")
        );

        swizzleAndAssert(b, e,
                input(b + "A" + e + b + "BB" + e),
                expected("A", "BB")
        );

        swizzleAndAssert(b, e,
                input(b + "" + e + b + "BB" + e),
                expected("", "BB")
        );

        swizzleAndAssert(b, e,
                input(b + "" + e + b + "BB"),
                expected("")
        );

        swizzleAndAssert(b, e,
                input(b + "ONE" + e + b + "BB"),
                expected("ONE")
        );

        swizzleAndAssert(b, e,
                input(b + b + "ONE" + e + e + b + "BB"),
                expected(b + "ONE")
        );

    }

    private String input(final String... strings) {
        return Join.join("", strings);
    }

    private String[] expected(final String... strings) {
        return strings;
    }

    public void testUnterminatedString() throws Exception {
        swizzleAndAssert("{", "}", "{hello");
    }

    private void swizzleAndAssert(String begin, String end, String original, String... expected) throws IOException {

        {
            final List<String> actual = new ArrayList<>();
            InputStream in = StreamUtils.stringToStream(original);
            in = new DelimitedTokenWatchInputStream(in, begin, end, actual::add);
            final String completed = StreamUtils.streamToString(in);

            assertEquals(Join.join("\n", expected), Join.join("\n", actual));
            assertEquals(original, completed);
        }
        {
            final List<String> actual = new ArrayList<>();
            InputStream in = StreamUtils.stringToStream(original);
            in = new DelimitedTokenWatchInputStream(in, begin, end, actual::add);
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