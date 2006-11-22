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

import org.codehaus.swizzle.stream.DelimitedTokenReplacementInputStream;
import org.codehaus.swizzle.stream.StreamTokenHandler;
import org.codehaus.swizzle.stream.StringTokenHandler;

public class DelimitedTokenReplacementInputStreamTest extends TestCase {

    StreamTokenHandler testTokenHandler = new TestTokenHandler();


    public void testTokenizingFilterInputStream() throws Exception {
        String original = "";
        String expected = "";

        original = "some {FOO} text";
        expected = "some apple text";
        swizzleAndAssert(original, expected);

        original = "{FOO} text";
        expected = "apple text";
        swizzleAndAssert(original, expected);

        original = "some text {FOO}";
        expected = "some text apple";
        swizzleAndAssert(original, expected);

        original = "{FOO}";
        expected = "apple";
        swizzleAndAssert(original, expected);

        original = "some {FOO}O text";
        expected = "some appleO text";
        swizzleAndAssert(original, expected);

        original = "some text";
        expected = "some text";
        swizzleAndAssert(original, expected);

        original = "";
        expected = "";
        swizzleAndAssert(original, expected);

        original = "some FO text";
        expected = "some FO text";
        swizzleAndAssert(original, expected);

        original = "FO some text";
        expected = "FO some text";
        swizzleAndAssert(original, expected);

        original = "some text FO";
        expected = "some text FO";
        swizzleAndAssert(original, expected);

        original = "some {FOO} {FOO} text";
        expected = "some apple apple text";
        swizzleAndAssert(original, expected);

        original = "{FOO} text {FOO}";
        expected = "apple text apple";
        swizzleAndAssert(original, expected);

        original = "{FOO}{FOO}";
        expected = "appleapple";
        swizzleAndAssert(original, expected);

        original = "BAsome {FOO}O text";
        expected = "BAsome appleO text";
        swizzleAndAssert(original, expected);

        original = "{BAR}{FOO}{FOO}{BAR} text";
        expected = "orangeappleappleorange text";
        swizzleAndAssert(original, expected);

        original = "{BAR} some {FOO} {BAR} test {FOO}";
        expected = "orange some apple orange test apple";
        swizzleAndAssert(original, expected);
    }

    private void swizzleAndAssert(String original, String expected) throws IOException {
        InputStream in = StreamUtils.stringToStream(original);
        in = new DelimitedTokenReplacementInputStream(in, "{", "}", testTokenHandler);
        String actual = StreamUtils.streamToString(in);

        assertEquals(expected, actual);
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