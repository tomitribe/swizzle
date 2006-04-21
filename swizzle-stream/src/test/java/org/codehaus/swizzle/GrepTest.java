/**
 *
 * Copyright 2004 David Blevins
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
package org.codehaus.swizzle;

import junit.framework.TestCase;
import org.codehaus.swizzle.Grep;

/**
 * @version $Rev$ $Date$
 */
public class GrepTest extends TestCase {

    private String source = "a0\na1\na2\nb0\nb1\nb2\nc0\nc1\nc2\nd0\nd1\nd2\ne0\ne1\ne2\nf0\nf1\nf2\n";

    public void testFilter() throws Exception
    {
        assertEquals("", grep(0, "x1"));

        assertEquals("c1\n", grep(0, "c1"));

        assertEquals("c0\nc1\nc2\n", grep(1, "c1"));

        assertEquals("b2\nc0\nc1\nc2\nd0\n", grep(2, "c1"));

        assertEquals("a0\na1\n", grep(1, "a0"));

        assertEquals("f1\nf2\n", grep(1, "f2"));

        assertEquals("c0\nc1\nc2\n", grep(0, "c[012]"));

        assertEquals("b2\nc0\nc1\nc2\nd0\n", grep(1, "c[012]"));

        assertEquals("b1\nb2\nc0\nc1\nc2\nd0\nd1\n", grep(2, "c[012]"));

        assertEquals("a1\nc1\ne1\n", grep(0, "[a,c,e]1"));

        assertEquals("a0\na1\na2\n--\nc0\nc1\nc2\n--\ne0\ne1\ne2\n", grep(1, "[a,c,e]1"));

        assertEquals("a0\na1\na2\nb0\n--\nb2\nc0\nc1\nc2\nd0\n--\nd2\ne0\ne1\ne2\nf0\n", grep(2, "[a,c,e]1"));

        assertEquals("a0\na1\na2\nb0\nb1\nb2\nc0\nc1\nc2\nd0\nd1\nd2\ne0\ne1\ne2\nf0\nf1\n", grep(3, "[a,c,e]1"));
    }

    private String grep(int context, String regex)
    {
        Grep filter = new Grep(regex, context);
        return filter.filter(source);
    }
}
