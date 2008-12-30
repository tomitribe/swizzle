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
import java.net.URL;

public class ResolveUrlInputStreamTest extends TestCase {

    public void testLinkFilterInputStream1() throws Exception {
        URL url = new URL("http://swizzle.codehaus.org/stuff/");

        String original = "<tr><td align=\"left\">\n<a href=\"/section1/orange.html\"><img src=\"/images/icon.gif\"\n border=\"0\"> Link Text</a></td>";
        String exptected = "<tr><td align=\"left\">\n<a href=\"http://swizzle.codehaus.org/section1/orange.html\"><img src=\"http://swizzle.codehaus.org/images/icon.gif\"\n border=\"0\"> Link Text</a></td>";

        String actual = resolveURLs(original, url);

        assertEquals(exptected, actual);
    }

    public void testLinkFilterInputStream2() throws Exception {
        URL url = new URL("http://swizzle.codehaus.org/stuff/");

        String original = "<td align=\"left\"><a href=\"apple.html\"><img src=\"images/picture.gif\" border=\"0\"> Link Text</a></td>";
        String exptected = "<td align=\"left\"><a href=\"http://swizzle.codehaus.org/stuff/apple.html\"><img src=\"http://swizzle.codehaus.org/stuff/images/picture.gif\" border=\"0\"> Link Text</a></td>";

        String actual = resolveURLs(original, url);

        assertEquals(exptected, actual);
    }

    public void testLinkFilterInputStream3() throws Exception {
        URL url = new URL("http://swizzle.codehaus.org/stuff/");

        String original = "<td align=\"left\">\n<a href=\"subsection/mango.html\"><img src=\"http://superimages.org/images/mango.jpg\" border=\"0\"> Link Text</a>\n</td>";
        String exptected = "<td align=\"left\">\n<a href=\"http://swizzle.codehaus.org/stuff/subsection/mango.html\"><img src=\"http://superimages.org/images/mango.jpg\" border=\"0\"> Link Text</a>\n</td>";

        String actual = resolveURLs(original, url);

        assertEquals(exptected, actual);
    }


    private String resolveURLs(String original, URL url) throws IOException {
        InputStream in = StreamUtils.stringToStream(original);
        in = new ResolveUrlInputStream(in, "<A HREF=", ">", url);
        in = new org.codehaus.swizzle.stream.ResolveUrlInputStream(in, "SRC=\"", "\"", url);

        return StreamUtils.streamToString(in);
    }

}