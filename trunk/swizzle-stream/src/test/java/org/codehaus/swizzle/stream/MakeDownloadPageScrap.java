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
package org.codehaus.swizzle.stream;

import junit.framework.TestCase;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @version $Revision$ $Date$
 */
public class MakeDownloadPageScrap extends TestCase {

    public static void main(String[] args) throws Exception {
        new MakeDownloadPageScrap().testFoo();
    }

    public void testFoo() throws Exception {
        URL url = new URL("http://www.ibiblio.org/maven2/org/codehaus/swizzle/");
        InputStream in = url.openStream();
        in = new BufferedInputStream(in);

        StreamLexer lexer = new StreamLexer(in);

        lexer.readToken("Parent Directory");

        while (lexer.readToken("href") != null) {
            String link = lexer.readToken("\"", "/\"");
            String date = lexer.readToken("</a>", " -");
            date = date.trim();

            System.out.println("h3. " + link);

            URL artifactUrl = new URL(url, link);
            print(artifactUrl);
            System.out.println("");

        }

        fail("");

    }

    private void print(URL url) throws Exception {
        InputStream in = url.openStream();
        in = new BufferedInputStream(in);

        StreamLexer lexer = new StreamLexer(in);

        lexer.readToken("Parent Directory");

        while (lexer.readToken("folder.gif") != null) {
            String link = lexer.readToken("href=\"", "/\"");
            String date = lexer.readToken("</a>", " -");
            date = date.trim();
            System.out.println("* [" + link + "|" + url.toExternalForm() + "]  -  " + date);
        }

    }
}
