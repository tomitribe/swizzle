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

import java.net.URL;

/**
 * @version $Revision$ $Date$
 */
public class StreamLexerTest extends TestCase {

    public void _test() throws Exception {}
    
    /*
    <project>
      <dependencies>
        <dependency>
          <groupId>organge</groupId>
          <artifactId>orange-artifact</artifactId>
          <scope>test</scope>
        </dependency>
      </dependencies>
      <dependencies>
        <dependency>
          <groupId>yellow</groupId>
          <artifactId>yellow-artifact</artifactId>
          <version>2.3</version>
        </dependency>
      </dependencies>
    </project>
     */
    public void test() throws Exception {

        URL url = this.getClass().getClassLoader().getResource("lexer/test.xml");
        StreamLexer lexer = new StreamLexer(url.openStream());

        // step into <project>
        assertNotNull(lexer.read("<project>"));

        // step into <dependencies>
        assertNotNull(lexer.read("<dependencies>"));
        lexer.mark("</dependencies>");

        // step into the first <dependency>
        assertNotNull(lexer.seek("<dependency>"));
        lexer.mark("</dependency>");

        // read dependency information
        assertEquals("test", lexer.peek("<scope>", "</scope>"));
        assertEquals("orange", lexer.read("<groupId>", "</groupId>"));
        assertEquals("orange-artifact", lexer.read("<artifactId>", "</artifactId>"));

        // attempt to read the version element which is not present
        assertEquals(null, lexer.seek("<version>", "</version>"));

        // step out of <dependency>
        assertNotNull(lexer.read("</dependency>"));
        lexer.unmark();

        // step into the second <dependency>
        assertNull(lexer.seek("<dependency>"));

        assertNotNull(lexer.seek("</dependencies>"));
        lexer.unmark();

        // Another dependencies section?  [yes]
        assertNotNull(lexer.seek("<dependencies>"));
        lexer.mark("</dependencies>");

        assertNotNull(lexer.seek("<dependency>"));
        lexer.mark("</dependency>");

        assertNull(lexer.peek("<scope>", "</scope>"));
        assertEquals("yellow", lexer.read("<groupId>", "<groupId>"));
        assertEquals("yellow-artifact", lexer.read("<artifactId>", "</artifactId>"));
        assertEquals("2.3", lexer.seek("<version>", "</version>"));

        assertNotNull(lexer.read("</dependency>"));
        lexer.unmark();

        assertNull(lexer.seek("<dependency>"));

        assertNotNull(lexer.seek("</dependencies>"));
        lexer.unmark();

        assertNotNull(lexer.seek("</project>"));
    }
}
