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

import java.util.HashMap;

public class StringTemplateTest extends TestCase {
    public void testApply() throws Exception {
        StringTemplate dataMask = new StringTemplate("{dir}/{name}.{ext}");

        HashMap map = new HashMap();
        map.put("dir", "foo");
        map.put("name", "bar");
        map.put("ext", "txt");

        assertEquals("foo/bar.txt",dataMask.apply(map));
    }
}