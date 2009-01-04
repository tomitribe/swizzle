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

import java.io.InputStream;
import java.util.HashMap;

public class ExecuteMacroInputStreamTest extends TestCase {

    public void testExecuteMacroInputStream() throws Exception {
        HashMap macros = new HashMap();
        macros.put("wget", new ExecuteMacroInputStream.IncludeUrlMacro());
        macros.put("file", new ExecuteMacroInputStream.IncludeFileMacro());

        String original = "Some template {date:tz=PST}.  With some web content {wget:url=file:target/test-classes/fuzzbucket/widget.txt} and \n{file:path=target/test-classes/fuzzbucket/DoHickey.java.txt}";

        InputStream in = StreamUtils.stringToStream(original);
        in = new ExecuteMacroInputStream(in, "{", "}", macros);

        String actual = StreamUtils.streamToString(in);
        String expected = "Some template {date:tz=PST}.  With some web content This content is from the widget.txt file and \n"
                + "public class DoHickey {\n"
                + "    public String whatIsIt(){\n"
                + "        return \"i don't know\";\n" + "    }\n" + "}";

        assertEquals(expected, actual);
    }
}