/**
 * Copyright 2022 David Blevins
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.swizzle.stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScanBufferValueTest {

    @Test
    public void testValueAdd() {
        final ScanBuffer2.Value value = new ScanBuffer2.Value(5);

        assertEquals(-1, value.add('g'));
        assertEquals(-1, value.add('r'));
        assertEquals(-1, value.add('e'));
        assertEquals(-1, value.add('e'));
        assertEquals(-1, value.add('n'));
        assertEquals('g', value.add('l'));
        assertEquals('r', value.add('a'));
        assertEquals('e', value.add('n'));
        assertEquals('e', value.add('t'));
        assertEquals('n', value.add('e'));
        assertEquals('l', value.add('r'));
        assertEquals('a', value.add('n'));
        assertEquals('n', value.add(-1));
        assertEquals('t', value.add(-1));
        assertEquals('e', value.add(-1));
        assertEquals('r', value.add(-1));
        assertEquals('n', value.add(-1));
        assertEquals(-1, value.add(-1));
    }


    @Test
    public void drainToEmpty() {
        final ScanBuffer2.Value value = new ScanBuffer2.Value(3);

        value.add('r');
        value.add('e');
        value.add('d');

        assertEquals("red", value.toString());

        value.drain();
        assertEquals("ed", value.toString());

        value.drain();
        assertEquals("d", value.toString());

        value.drain();
        assertEquals("", value.toString());

        value.drain();
        assertEquals("", value.toString());

        value.add('y');
        assertEquals("y", value.toString());
    }

    @Test
    public void testValueLength() {
        final ScanBuffer2.Value value = new ScanBuffer2.Value(5);

        assertEquals(0, value.length());

        value.add('g');
        assertEquals(1, value.length());

        value.add('r');
        assertEquals(2, value.length());

        value.add('e');
        assertEquals(3, value.length());

        value.add('e');
        assertEquals(4, value.length());

        value.add('n');
        assertEquals(5, value.length());

        value.add('l');
        assertEquals(5, value.length());

        value.add('a');
        assertEquals(5, value.length());

        value.add('n');
        assertEquals(5, value.length());

        value.add(-1);
        assertEquals(4, value.length());

        value.add(-1);
        assertEquals(3, value.length());

        value.add(-1);
        assertEquals(2, value.length());

        value.add(-1);
        assertEquals(1, value.length());

        value.add(-1);
        assertEquals(0, value.length());

        value.add(-1);
        assertEquals(0, value.length());
    }

    @Test
    public void testToString() {
        final ScanBuffer2.Value value = new ScanBuffer2.Value(6);

        assertEquals("", value.toString());

        value.add('y');
        assertEquals("y", value.toString());

        value.add('e');
        assertEquals("ye", value.toString());

        value.add('l');
        assertEquals("yel", value.toString());

        value.add('l');
        assertEquals("yell", value.toString());

        value.add('o');
        assertEquals("yello", value.toString());

        value.add('w');
        assertEquals("yellow", value.toString());

        value.add('s');
        assertEquals("ellows", value.toString());

        value.add('u');
        assertEquals("llowsu", value.toString());

        value.add('b');
        assertEquals("lowsub", value.toString());

        value.add('m');
        assertEquals("owsubm", value.toString());

        value.add(-1);
        assertEquals("wsubm", value.toString());

        value.add(-1);
        assertEquals("subm", value.toString());

        value.drain();
        assertEquals("ubm", value.toString());

        value.drain();
        assertEquals("bm", value.toString());
    }
}