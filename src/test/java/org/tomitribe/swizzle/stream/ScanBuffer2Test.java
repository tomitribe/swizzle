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

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScanBuffer2Test {

    @Test
    public void append() {
        final ScanBuffer2 buffer = new ScanBuffer2("green", true);

        buffer.append('g');
        assertEquals(1, buffer.available());
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());
        assertEquals("g", buffer.asString());

        buffer.append('r');
        assertEquals(2, buffer.available());
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());
        assertEquals("gr", buffer.asString());

        buffer.append('o');
        assertEquals(3, buffer.available());
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());
        assertEquals("gro", buffer.asString());

        assertEquals('g', buffer.drain());
        assertEquals(2, buffer.available());
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());
        assertEquals("ro", buffer.asString());
    }

    @Test
    public void appendResult() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        assertEquals(-1, buffer.append('y'));
        assertEquals(-1, buffer.append('e'));
        assertEquals(-1, buffer.append('s'));
        assertEquals('y', buffer.append('n'));
        assertEquals('e', buffer.append('o'));
        assertEquals('s', buffer.append('m'));
        assertEquals('n', buffer.append('a'));
        assertEquals('o', buffer.append('y'));
        assertEquals('m', buffer.append('b'));
        assertEquals('a', buffer.append('e'));
    }

    @Test
    public void asString() {
        final ScanBuffer2 buffer = new ScanBuffer2("green", true);

        buffer.append('y');
        assertEquals("y", buffer.asString());

        buffer.append('e');
        assertEquals("ye", buffer.asString());

        buffer.append('l');
        assertEquals("yel", buffer.asString());

        buffer.append('l');
        assertEquals("yell", buffer.asString());

        buffer.append('o');
        assertEquals("yello", buffer.asString());

        buffer.append('w');
        assertEquals("ellow", buffer.asString());

        buffer.drain();
        assertEquals("llow", buffer.asString());

        buffer.drain();
        assertEquals("low", buffer.asString());

        buffer.append(' ');
        assertEquals("low ", buffer.asString());
    }

    @Test
    public void matching() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        buffer.append('r');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('e');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('d');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

        buffer.append('d');
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());

        //
        buffer.drain();
        buffer.drain();
        buffer.drain();

        buffer.append('r');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('e');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('d');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

    }

    @Test
    public void caseInsensitive1() {
        final ScanBuffer2 buffer = new ScanBuffer2("RED", false);

        buffer.append('r');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('e');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('d');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

        buffer.append('d');
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());

        //
        buffer.drain();
        buffer.drain();
        buffer.drain();

        buffer.append('r');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('e');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('d');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

    }

    @Test
    public void caseInsensitive2() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", false);

        buffer.append('R');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('E');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('D');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

        buffer.append('D');
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());

        //
        buffer.drain();
        buffer.drain();
        buffer.drain();

        buffer.append('R');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('E');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('D');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());

    }

    @Test
    public void matching2() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        buffer.append('r');
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('r');
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('e');
        assertFalse(buffer.matching());
        assertFalse(buffer.matches());

        buffer.drain();
        assertTrue(buffer.matching());
        assertFalse(buffer.matches());

        buffer.append('d');
        assertTrue(buffer.matching());
        assertTrue(buffer.matches());
    }

    @Test
    public void available() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        assertEquals(0, buffer.available());

        buffer.append('r');
        assertEquals(1, buffer.available());

        buffer.append(' ');
        assertEquals(2, buffer.available());

        buffer.drain();
        assertEquals(1, buffer.available());

        buffer.drain();
        assertEquals(0, buffer.available());

        buffer.drain();
        assertEquals(0, buffer.available());

        buffer.append(' ');
        assertEquals(1, buffer.available());

        buffer.append(' ');
        assertEquals(2, buffer.available());

        buffer.append(' ');
        assertEquals(3, buffer.available());

        buffer.append(' ');
        assertEquals(3, buffer.available());
    }

    @Test
    public void available2() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        assertEquals(0, buffer.available());
        assertEquals("", buffer.asString());

        buffer.append(-1);
        assertEquals(0, buffer.available());
        assertEquals("", buffer.asString());

        buffer.append(-1);
        assertEquals(0, buffer.available());
        assertEquals("", buffer.asString());

        buffer.append(-1);
        assertEquals(0, buffer.available());
        assertEquals("", buffer.asString());
    }

    @Test
    @Ignore
    public void matchingToEndOfStream() {
        final ScanBuffer2 buffer = new ScanBuffer2("red", true);

        buffer.append('r');
        buffer.append('e');
        buffer.append('d');

        assertTrue(buffer.matches());
        assertTrue(buffer.matching());
        assertEquals(3, buffer.available());
        assertEquals("red", buffer.asString());

        buffer.append('r');

        assertEquals("edr", buffer.asString());
        assertEquals(3, buffer.available());
        assertFalse(buffer.matches());
        assertFalse(buffer.matching());

    }

    @Test
    public void testIndex() {
        final ScanBuffer2.Index index = new ScanBuffer2.Index(5);

        assertEquals(0, index.get());
        assertEquals(1, index.increment());
        assertEquals(1, index.get());
        assertEquals(2, index.increment());
        assertEquals(2, index.get());
        assertEquals(3, index.increment());
        assertEquals(3, index.get());
        assertEquals(4, index.increment());
        assertEquals(4, index.get());
        assertEquals(0, index.increment());
        assertEquals(0, index.get());

    }

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
}