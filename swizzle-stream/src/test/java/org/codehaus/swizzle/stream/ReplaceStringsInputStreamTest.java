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

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ReplaceStringsInputStreamTest extends TestCase {

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
        InputStream in = StreamUtils.stringToStream(original);

        Map strings = new HashMap();
        strings.put("GREEN", "grape");
        strings.put("RED", "pear");
        strings.put("BLUE", "banana");
        in = new ReplaceStringsInputStream(in, strings);

        String actual = StreamUtils.streamToString(in);

        assertEquals(expected, actual);
    }

    public void testFileStreamReplacement() throws Exception {
        Map strings = new HashMap();
        strings.put("java", "org.java");
        strings.put("org.codehaus", "biz.codehizzle");
        strings.put("Copyright", "Copyrizzle");
        strings.put("public", "private");
        strings.put("private", "public");
        strings.put("Token", "ParsedString");
        strings.put("token", "parsedString");
        strings.put("parent", "parentURL");
        strings.put("Url", "URL");
        strings.put("begin", "startText");
        strings.put("link", "location");


        File original = new File("target/test-classes/fixedtoken/ResolveUrlInputStream.original.java.txt");
        File expected = new File("target/test-classes/fixedtoken/ResolveUrlInputStream.expected.java.txt");
        File actual = new File("target/test-classes/fixedtoken/ResolveUrlInputStream.actual.java.txt");

        InputStream in = new FileInputStream(original);
        in = new ReplaceStringsInputStream(in, strings);

        FileOutputStream out = new FileOutputStream(actual);

        int b = in.read();
        while (b != -1) {
            out.write(b);
            b = in.read();
        }
        in.close();
        out.close();


        String expectedContent = StreamUtils.streamToString(new FileInputStream(expected));
        String actualContent = StreamUtils.streamToString(new FileInputStream(actual));
        assertEquals(expectedContent, actualContent);
    }

}
