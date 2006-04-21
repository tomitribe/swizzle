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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.codehaus.swizzle.stream.ReplaceStringInputStream;

public class ReplaceStringInputStreamTest extends TestCase {

    public void testTokenFilterInputStream() throws Exception {
        String original = "";
        String expected = "";
        swizzleAndAssert(original, expected);

        original = "x";
        expected = "x";
        swizzleAndAssert(original, expected);

        original = "abcdefghijklmnop";
        expected = "abcdefghijklmnop";
        swizzleAndAssert(original, expected);

        original = "RED";
        expected = "pear";
        swizzleAndAssert(original, expected);

        original = "aRED";
        expected = "apear";
        swizzleAndAssert(original, expected);

        original = "REDb";
        expected = "pearb";
        swizzleAndAssert(original, expected);

        original = "aREDz";
        expected = "apearz";
        swizzleAndAssert(original, expected);

        original = "abREDz";
        expected = "abpearz";
        swizzleAndAssert(original, expected);

        original = "abREDyz";
        expected = "abpearyz";
        swizzleAndAssert(original, expected);

        original = "abcREDwxyz";
        expected = "abcpearwxyz";
        swizzleAndAssert(original, expected);

        original = "abcdREDwxyz";
        expected = "abcdpearwxyz";
        swizzleAndAssert(original, expected);

        original = "abcdeREDwxyz";
        expected = "abcdepearwxyz";
        swizzleAndAssert(original, expected);

        original = "abcdefghiREDwxyz";
        expected = "abcdefghipearwxyz";
        swizzleAndAssert(original, expected);

        original = "abcdefghiREDstuvwxyz";
        expected = "abcdefghipearstuvwxyz";
        swizzleAndAssert(original, expected);

        original = "REDBLUE";
        expected = "pearbanana";
        swizzleAndAssert(original, expected);

        original = "aREDBLUE";
        expected = "apearbanana";
        swizzleAndAssert(original, expected);

        original = "REDBLUEb";
        expected = "pearbananab";
        swizzleAndAssert(original, expected);

        original = "aREDzBLUE";
        expected = "apearzbanana";
        swizzleAndAssert(original, expected);

        original = "BLUEabREDz";
        expected = "bananaabpearz";
        swizzleAndAssert(original, expected);

        original = "abBLUEREDyzGREEN";
        expected = "abbananapearyzgrape";
        swizzleAndAssert(original, expected);

        original = "abcdefghiGREENjklREDwxyz";
        expected = "abcdefghigrapejklpearwxyz";
        swizzleAndAssert(original, expected);

        original = "abcGREENGREENdeBLUEBLUEfGREENBLUEghiREDstuvwxyz";
        expected = "abcgrapegrapedebananabananafgrapebananaghipearstuvwxyz";
        swizzleAndAssert(original, expected);

    }

    private void swizzleAndAssert(String original, String expected) throws IOException {
        InputStream in = TestUtil.stringToStream(original);

        in = new ReplaceStringInputStream(in, "RED", "pear");
        in = new ReplaceStringInputStream(in, "GREEN", "grape");
        in = new ReplaceStringInputStream(in, "BLUE", "banana");

        String actual = TestUtil.streamToString(in);

        assertEquals(expected, actual);
    }

    public void testFileStreamReplacement() throws Exception {
        File original = new File("target/test-classes/fixedtoken/PipedTokenReplacement.original.java.txt");
        File expected = new File("target/test-classes/fixedtoken/PipedTokenReplacement.expected.java.txt");
        File actual = new File("target/test-classes/fixedtoken/PipedTokenReplacement.actual.java.txt");

        // Notice that this output results in all varaibles and accessors
        // being scoped at "public".  This is a piped setup, so the output
        // of one ReplaceStringInputStream is the input of another.
        // On of them turns all occurrences of "public" into "private"
        // The next one turns all occurrences "private" into "public"
        // The result after that is everything is marked "public"
        InputStream in = new FileInputStream(original);
        in = new ReplaceStringInputStream(in, "java", "org.java");
        in = new ReplaceStringInputStream(in, "org.codehaus", "biz.codehizzle");
        in = new ReplaceStringInputStream(in, "Copyright", "Copyrizzle");
        in = new ReplaceStringInputStream(in, "public", "private");
        in = new ReplaceStringInputStream(in, "private", "public"); // Guess what will happen here
        in = new ReplaceStringInputStream(in, "Token", "ParsedString");
        in = new ReplaceStringInputStream(in, "token", "parsedString");
        in = new ReplaceStringInputStream(in, "parent", "parentURL");
        in = new ReplaceStringInputStream(in, "Url", "URL");
        in = new ReplaceStringInputStream(in, "begin", "startText");
        in = new ReplaceStringInputStream(in, "link", "location");

        FileOutputStream out = new FileOutputStream(actual);

        int b = in.read();
        while (b != -1) {
            out.write(b);
            b = in.read();
        }
        in.close();
        out.close();


        String expectedContent = TestUtil.streamToString(new FileInputStream(expected));
        String actualContent = TestUtil.streamToString(new FileInputStream(actual));
        assertEquals(expectedContent, actualContent);
    }

}
