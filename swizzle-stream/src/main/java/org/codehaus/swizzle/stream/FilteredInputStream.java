package org.codehaus.swizzle.stream;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FilteredInputStream extends FilterInputStream {

    private boolean done = false;

    public FilteredInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {

        int count = 0;

        if (done) {
            return (-1);
        }

        for (int i = off, max = off + len; i < max; i++) {

            final int read = read();

            if (read == -1) {
                done = true;
                return count == 0 ? -1 : count;
            }

            bytes[i] = (byte) read;
            count++;
        }

        return count;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
