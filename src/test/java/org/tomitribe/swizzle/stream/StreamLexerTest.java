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
package org.tomitribe.swizzle.stream;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class StreamLexerTest extends TestCase {

    public void _test() throws Exception {
    }

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
    public void testMarkUnmark() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("lexer/test.xml");
        StreamLexer lexer = new StreamLexer(url.openStream());

        // step into <project>
        assertNotNull(lexer.read("<project>"));
        lexer.mark("</project>");

        // step into first <dependencies>
        assertNotNull(lexer.read("<dependencies>"));
        lexer.mark("</dependencies>");

        // step into <dependency>
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

        // make sure there isn't another <dependency> section
        assertNull(lexer.seek("<dependency>"));

        // step out of first <dependencies>
        assertNotNull(lexer.seek("</dependencies>"));
        lexer.unmark();

        // Another dependencies section?  [yes]
        // step into second <dependencies>
        assertNotNull(lexer.seek("<dependencies>"));
        lexer.mark("</dependencies>");

        // step into <dependency>
        assertNotNull(lexer.seek("<dependency>"));
        lexer.mark("</dependency>");

        // read dependency information
        assertNull(lexer.peek("<scope>", "</scope>"));
        assertEquals("yellow", lexer.read("<groupId>", "</groupId>"));
        assertEquals("yellow-artifact", lexer.read("<artifactId>", "</artifactId>"));
        assertEquals("2.3", lexer.seek("<version>", "</version>"));

        // step out of <dependency>
        assertNotNull(lexer.read("</dependency>"));
        lexer.unmark();

        // make sure there isn't another <dependency> section
        assertNull(lexer.seek("<dependency>"));

        // step out of first <dependencies>
        assertNotNull(lexer.seek("</dependencies>"));
        lexer.unmark();

        // step out of first <project>
        assertNotNull(lexer.seek("</project>"));
    }

    public void testSeenAndMark() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("lexer/test.xml");
        StreamLexer lexer = new StreamLexer(url.openStream());

        // step into <project>
        assertTrue(lexer.seekAndMark("<project>", "</project>"));

        // step into first <dependencies>
        assertTrue(lexer.seekAndMark("<dependencies>", "</dependencies>"));

        // step into <dependency>
        assertTrue(lexer.seekAndMark("<dependency>", "</dependency>"));

        // read dependency information
        assertEquals("test", lexer.peek("<scope>", "</scope>"));
        assertEquals("orange", lexer.read("<groupId>", "</groupId>"));
        assertEquals("orange-artifact", lexer.read("<artifactId>", "</artifactId>"));

        // attempt to read the version element which is not present
        assertEquals(null, lexer.seek("<version>", "</version>"));

        // step out of <dependency>
        assertTrue(lexer.readAndUnmark());

        // make sure there isn't another <dependency> section
        assertFalse(lexer.seekAndMark("<dependency>", "</dependency>"));

        // step out of first <dependencies>
        assertTrue(lexer.readAndUnmark());

        // Another dependencies section?  [yes]
        // step into second <dependencies>
        assertTrue(lexer.seekAndMark("<dependencies>", "</dependencies>"));

        // step into <dependency>
        assertTrue(lexer.seekAndMark("<dependency>", "</dependency>"));

        // read dependency information
        assertNull(lexer.peek("<scope>", "</scope>"));
        assertEquals("yellow", lexer.read("<groupId>", "</groupId>"));
        assertEquals("yellow-artifact", lexer.read("<artifactId>", "</artifactId>"));
        assertEquals("2.3", lexer.seek("<version>", "</version>"));

        // step out of <dependency>
        assertTrue(lexer.readAndUnmark());

        // make sure there isn't another dependency section
        assertFalse(lexer.seekAndMark("<dependency>", "</dependency>"));

        // step out of first <dependencies>
        assertTrue(lexer.readAndUnmark());

        // step out of first <project>
        assertTrue(lexer.readAndUnmark());
    }


    public void testSeenAndMarkWithRecursiveMethod() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("lexer/test.xml");
        StreamLexer lexer = new StreamLexer(url.openStream());

        // step into <project>
        assertTrue(lexer.seekAndMark("<project>", "</project>"));

        // verify first dependency set
        List<Map<String, String>> firstDendencies = readDependencies(lexer);
        assertNotNull("no dependencies", firstDendencies);
        assertEquals("first dependencies size", 1, firstDendencies.size());
        assertDependency("orange", "orange-artifact", null, "test", firstDendencies.get(0));

        // verify second dependency set
        List<Map<String, String>> secondDendencies = readDependencies(lexer);
        assertNotNull("second dependency set not found", secondDendencies);
        assertEquals("second dependencies size", 1, secondDendencies.size());
        assertDependency("yellow", "yellow-artifact", "2.3", null, secondDendencies.get(0));

        // assure there are no more dependency sets
        List<Map<String, String>> thirdDendencies = readDependencies(lexer);
        assertNull("third dependency set found", thirdDendencies);

        // step out of first <project>
        assertTrue(lexer.readAndUnmark());
    }

    private List<Map<String, String>> readDependencies(StreamLexer lexer) throws IOException {
        // step into first <dependencies>
        if (!lexer.seekAndMark("<dependencies>", "</dependencies>")) return null;

        List<Map<String, String>> dependencies = new ArrayList<Map<String, String>>();

        // step into each <dependency>
        while (lexer.seekAndMark("<dependency>", "</dependency>")) {
            dependencies.add(readDependency(lexer));

            // step out of <dependency>
            assertTrue(lexer.readAndUnmark());
        }

        // step out of first <dependencies>
        assertTrue(lexer.readAndUnmark());

        return dependencies;
    }

    private Map<String, String> readDependency(StreamLexer lexer) throws IOException {
        // read dependency
        Map<String, String> dependency = new HashMap<String, String>();
        dependency.put("scope", lexer.peek("<scope>", "</scope>"));
        dependency.put("groupId", lexer.peek("<groupId>", "</groupId>"));
        dependency.put("artifactId", lexer.peek("<artifactId>", "</artifactId>"));
        dependency.put("version", lexer.peek("<version>", "</version>"));
        return dependency;
    }

    private void assertDependency(String groupId, String artifactId, String version, String scope, Map<String, String> dependency) {
        assertEquals(groupId, dependency.get("groupId"));
        assertEquals(artifactId, dependency.get("artifactId"));
        assertEquals(version, dependency.get("version"));
        assertEquals(scope, dependency.get("scope"));
    }

    public void testEmptyStringRead() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("lexer/simpletext");
        StreamLexer lexer = new StreamLexer(url.openStream());

        String s = "\n";

        assertEquals("one", lexer.read("", s));
        assertEquals("two", lexer.read("", s));
        assertEquals("three", lexer.read("", s));
        assertEquals("four", lexer.read("", s));
        assertEquals("five", lexer.read("", s));

    }

}
