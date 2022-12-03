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
}