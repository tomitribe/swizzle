/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class StreamBuilder {

    private InputStream in;

    public StreamBuilder(InputStream in) {
        this.in = in;
    }

    public StreamBuilder include(final String begin, final String end) {
        return include(begin, end, true);
    }

    public StreamBuilder include(final String begin, final String end, final boolean caseSensitive) {
        return include(begin, end, caseSensitive, true);
    }

    public StreamBuilder include(final String begin, final String end, final boolean caseSensitive, final boolean retainDelimiters) {
        in = new IncludeFilterInputStream(in, begin, end, caseSensitive, retainDelimiters);
        return this;
    }

    public StreamBuilder exclude(final String begin, final String end) {
        return exclude(begin, end, false, true);
    }

    public StreamBuilder exclude(final String begin, final String end, final boolean caseSensitive) {
        return exclude(begin, end, caseSensitive, false);
    }

    public StreamBuilder exclude(final String begin, final String end, final boolean caseSensitive, final boolean retainDelimiters) {
        in = new ExcludeFilterInputStream(in, begin, end, caseSensitive, retainDelimiters);
        return this;
    }

    public StreamBuilder delete(final String token) {
        return replace(token, "");
    }

    public StreamBuilder delete(final String begin, final String end) {
        return replace(begin, end, "");
    }

    public StreamBuilder deleteBetween(final String begin, final String end) {
        return replaceBetween(begin, end, "");
    }

    public StreamBuilder replace(final String token, final String with) {
        return replace(token, s -> with);
    }

    public StreamBuilder replace(final String token, final StringHandler with) {
        in = new FixedTokenReplacementInputStream(in, token, with);
        return this;
    }

    public StreamBuilder replace(final String begin, final String end, final StringHandler handler) {
        in = new DelimitedTokenReplacementInputStream(in, begin, end, handler);
        return this;
    }

    public StreamBuilder replace(final String begin, final String end, final String with) {
        return replace(begin, end, s -> with);
    }

    public StreamBuilder replaceBetween(final String begin, final String end, final StringHandler handler) {
        final StringHandler includeDelimiters = s -> begin + handler.apply(s) + end;
        in = new DelimitedTokenReplacementInputStream(in, begin, end, includeDelimiters);
        return this;
    }

    public StreamBuilder replaceBetween(final String begin, final String end, final String with) {
        return replaceBetween(begin, end, s -> with);
    }

    public static StreamBuilder create(final InputStream in) {
        return new StreamBuilder(in);
    }

    public static StreamBuilder of(final InputStream in) {
        return new StreamBuilder(in);
    }

    public static StreamBuilder of(final File file) {
        try {
            final InputStream in = new FileInputStream(file);
            return new StreamBuilder(new BufferedInputStream(in, 8192 * 4));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static StreamBuilder of(final byte[] bytes) {
        return new StreamBuilder(new ByteArrayInputStream(bytes));
    }

    public static StreamBuilder of(final String contents) {
        return new StreamBuilder(new ByteArrayInputStream(contents.getBytes()));
    }

    public static StreamBuilder of(final String contents, final Charset charset) {
        return new StreamBuilder(new ByteArrayInputStream(contents.getBytes(charset)));
    }

    public StreamBuilder watch(final OutputStream consumer) {
        in = new WatchAllInputStream(in, consumer);
        return this;
    }

    public StreamBuilder watch(final String token, final Consumer<String> consumer) {
        in = new FixedTokenWatchInputStream(in, token, consumer);
        return this;
    }

    public StreamBuilder watch(final String token, final boolean caseSensitive, final Consumer<String> consumer) {
        in = new FixedTokenWatchInputStream(in, token, caseSensitive, consumer);
        return this;
    }

    public StreamBuilder watch(final String begin, final String end, final Consumer<String> consumer) {
        in = new DelimitedTokenWatchInputStream(in, begin, end, consumer);
        return this;
    }

    public StreamBuilder watch(final String begin, final String end, final boolean caseSensitive, final boolean includeDelimiters, final Consumer<String> consumer) {
        in = new DelimitedTokenWatchInputStream(in, begin, end, caseSensitive, includeDelimiters, consumer);
        return this;
    }

    public StreamBuilder watch(final String token, final Runnable runnable) {
        in = new FixedTokenWatchInputStream(in, token, runnable);
        return this;
    }

    public StreamBuilder watch(final String token, final boolean caseSensitive, final Runnable runnable) {
        in = new FixedTokenWatchInputStream(in, token, caseSensitive, runnable);
        return this;
    }

    public StreamBuilder watch(final String begin, final String end, final Runnable runnable) {
        in = new DelimitedTokenWatchInputStream(in, begin, end, runnable);
        return this;
    }

    public StreamBuilder watch(final String begin, final String end, final boolean caseSensitive, final boolean includeDelimiters, final Runnable runnable) {
        in = new DelimitedTokenWatchInputStream(in, begin, end, caseSensitive, includeDelimiters, runnable);
        return this;
    }

    public StreamBuilder substream(final String begin, final String end, final IOFunction<InputStream, InputStream> decorator) {
        in = substream(in, begin, end, decorator);
        return this;
    }

    public static InputStream substream(final InputStream in, final String begin, final String end, final IOFunction<InputStream, InputStream> decorator) {
        return new DelimitedTokenReplacementInputStream(in, begin, end, s -> {
            return decorator.apply(new ByteArrayInputStream(s.getBytes()));
        }
        );
    }

    public void to(final OutputStream out) throws IOException {
        copy(in, out);
    }

    public void run() throws IOException {
        to(new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
            }
        });
    }

    /**
     * Applies the function to the enclosed InputStream to consume
     * the data.  Any IOExceptions thrown by the IOFunction will be
     * converted to an UncheckedIOException.  The InputStream will
     * be closed after the IOFunction completes.
     */
    public <R> R apply(final IOFunction<InputStream, R> ioFunction) {
        try {
            return ioFunction.apply(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    /**
     * Uses the supplied IOConsumber to consume the enclosed InputStream
     * Any IOExceptions thrown by the IOConsumer will be converted to an
     * UncheckedIOException.  The InputStream will be closed after the
     * IOConsumer completes.
     */
    public void consume(final IOConsumer<InputStream> consumer) {
        try {
            consumer.accept(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class WatchAllInputStream extends InputStream {

        private final InputStream in;
        private final OutputStream consumer;

        public WatchAllInputStream(final InputStream in, final OutputStream consumer) {
            this.in = in;
            this.consumer = consumer;
        }

        @Override
        public int read() throws IOException {
            final int read = in.read();
            consumer.write(read);
            return read;
        }
    }

    public InputStream get() {
        return in;
    }

    private static void copy(final InputStream from, final OutputStream to) throws IOException {
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = from.read(buffer)) != -1) {
            to.write(buffer, 0, length);
        }
        to.flush();
    }
}
