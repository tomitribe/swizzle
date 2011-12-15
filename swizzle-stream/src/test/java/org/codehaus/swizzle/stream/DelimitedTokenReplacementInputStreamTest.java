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

/**
 * @version $Revision$ $Date$
 */

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

public class DelimitedTokenReplacementInputStreamTest extends TestCase {

    StreamTokenHandler testTokenHandler = new TestTokenHandler();

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
        String original = "";
        String expected = "";

        original = b + "FOO" + e + "abc";
        expected = "appleabc";
        swizzleAndAssert(original, expected, b, e);

        original = "a" + b + "FOO" + e + "bc";
        expected = "aapplebc";
        swizzleAndAssert(original, expected, b, e);

        original = "ab" + b + "FOO" + e + "c";
        expected = "abapplec";
        swizzleAndAssert(original, expected, b, e);

        original = "abc" + b + "FOO" + e;
        expected = "abcapple";
        swizzleAndAssert(original, expected, b, e);

        original = "some " + b + "FOO" + e + " text";
        expected = "some apple text";
        swizzleAndAssert(original, expected, b, e);

        original = b + "FOO" + e + " text";
        expected = "apple text";
        swizzleAndAssert(original, expected, b, e);

        original = "some text " + b + "FOO" + e;
        expected = "some text apple";
        swizzleAndAssert(original, expected, b, e);

        original = b + "FOO" + e;
        expected = "apple";
        swizzleAndAssert(original, expected, b, e);

        original = "some " + b + "FOO" + e + "O text";
        expected = "some appleO text";
        swizzleAndAssert(original, expected, b, e);

        original = "some text";
        expected = "some text";
        swizzleAndAssert(original, expected, b, e);

        original = "";
        expected = "";
        swizzleAndAssert(original, expected, b, e);

        original = "some FO text";
        expected = "some FO text";
        swizzleAndAssert(original, expected, b, e);

        original = "FO some text";
        expected = "FO some text";
        swizzleAndAssert(original, expected, b, e);

        original = "some text FO";
        expected = "some text FO";
        swizzleAndAssert(original, expected, b, e);

        original = "some " + b + "FOO" + e + " " + b + "FOO" + e + " text";
        expected = "some apple apple text";
        swizzleAndAssert(original, expected, b, e);

        original = b + "FOO" + e + " text " + b + "FOO" + e;
        expected = "apple text apple";
        swizzleAndAssert(original, expected, b, e);

        original = b + "FOO" + e + b + "FOO" + e;
        expected = "appleapple";
        swizzleAndAssert(original, expected, b, e);

        original = "BAsome " + b + "FOO" + e + "O text";
        expected = "BAsome appleO text";
        swizzleAndAssert(original, expected, b, e);

        original = b + "BAR" + e + b + "FOO" + e + b + "FOO" + e + b + "BAR" + e + " text";
        expected = "orangeappleappleorange text";
        swizzleAndAssert(original, expected, b, e);

        original = b + "BAR" + e + " some " + b + "FOO" + e + " " + b + "BAR" + e + " test " + b + "FOO" + e;
        expected = "orange some apple orange test apple";
        swizzleAndAssert(original, expected, b, e);
    }


    public void testUnterminatedString() throws Exception {
        swizzleAndAssert("{hello", "{hello", "{", "}");
    }

    private void swizzleAndAssert(String original, String expected, String begin, String end) throws IOException {
        InputStream in = StreamUtils.stringToStream(original);
        in = new DelimitedTokenReplacementInputStream(in, begin, end, testTokenHandler);
        String actual = StreamUtils.streamToString(in);

        assertEquals(expected, actual);

        // Now use a buffered method to do the same test.
        in = StreamUtils.stringToStream(original);
        in = new DelimitedTokenReplacementInputStream(in, begin, end, testTokenHandler);
        actual = streamToStringChunked(in);

        assertEquals(expected, actual);
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

    private static class TestTokenHandler extends StringTokenHandler {
        public String handleToken(String token) {
            if (token.equals("FOO")) {
                return "apple";
            } else if (token.equals("BAR")) {
                return "orange";
            } else {
                return token;
            }
        }
    }
}