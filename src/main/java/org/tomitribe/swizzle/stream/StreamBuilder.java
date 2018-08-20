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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Function;

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
        in = new ReplaceStringInputStream(in, token, "");
        return this;
    }

    public StreamBuilder deleteBetween(final String begin, final String end, final boolean caseSensitive) {
        return exclude(begin, end, caseSensitive, false);
    }

    public StreamBuilder deleteBetween(final String begin, final String end) {
        in = new ExcludeFilterInputStream(in, begin, end, true, true);
        return this;
    }

    public StreamBuilder replace(final String token, final String with) {
        in = new ReplaceStringInputStream(in, token, with);
        return this;
    }

    public static StreamBuilder create(final InputStream in) {
        return new StreamBuilder(in);
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

    public StreamBuilder substream(final String begin, final String end, final Function<InputStream, InputStream> decorator) {
        in = substream(in, begin, end, decorator);
        return this;
    }

    public static InputStream substream(final InputStream in, final String begin, final String end, final Function<InputStream, InputStream> decorator) {
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
