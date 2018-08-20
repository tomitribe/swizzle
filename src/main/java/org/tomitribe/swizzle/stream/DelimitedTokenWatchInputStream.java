/**
 *
 * Copyright 2003 Tomitribe
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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class DelimitedTokenWatchInputStream extends FilteredInputStream {

    private final Consumer<String> consumer;
    private final String begin;
    private final String end;
    private final boolean caseSensitive;
    private final boolean includeDelimiters;

    public DelimitedTokenWatchInputStream(final InputStream in, final String begin, final String end, final Runnable runnable) {
        this(in, begin, end, s -> runnable.run());
    }

    public DelimitedTokenWatchInputStream(final InputStream in, final String begin, final String end, final boolean caseSensitive, final boolean includeDelimiters, final Runnable runnable) {
        this(in, begin, end, caseSensitive, includeDelimiters, s -> runnable.run());
    }

    public DelimitedTokenWatchInputStream(final InputStream in, final String begin, final String end, final Consumer<String> consumer) {
        this(in, begin, end, true, false, consumer);
    }

    public DelimitedTokenWatchInputStream(final InputStream in, final String begin, final String end, final boolean caseSensitive, final boolean includeDelimiters, final Consumer<String> consumer) {
        super(in);
        this.caseSensitive = caseSensitive;
        this.consumer = consumer;
        this.begin = begin;
        this.end = end;
        this.includeDelimiters = includeDelimiters;
        this.strategy = new FindStart();
    }

    private Strategy strategy;

    public int read() throws IOException {
        return strategy.read();
    }

    // reading url (looking for end)
    // flushing url
    // regular read (looking for begin)
    @FunctionalInterface
    interface Strategy {
        int read() throws IOException;
    }

    private int superRead() throws IOException {
        return super.read();
    }

    public int done() {
        strategy = () -> -1;
        return -1;
    }

    private class FindStart implements Strategy {
        private final ScanBuffer beginBuffer = new ScanBuffer(begin, caseSensitive);

        public int read() throws IOException {
            if (beginBuffer.size() == 0) {
                strategy = new FindEnd();
                return strategy.read();
            }

            int stream = superRead();
            if (stream == -1) return done();

            beginBuffer.append(stream);

            if (beginBuffer.match()) {
                strategy = new FindEnd();
            }

            return stream;
        }
    }

    private class FindEnd implements Strategy {
        final StringBuilder token = new StringBuilder();
        private final ScanBuffer endBuffer = new ScanBuffer(end, caseSensitive);

        public FindEnd() {
            endBuffer.flush();
        }

        public int read() throws IOException {
            int stream = superRead();
            if (stream == -1) return done();

            token.append((char) stream);
            endBuffer.append(stream);
            if (endBuffer.match()) {
                strategy = new FindStart();

                final String substring = token.substring(0, token.length() - endBuffer.size());
                if (includeDelimiters) {
                    consumer.accept(begin + substring + end);
                } else {
                    consumer.accept(substring);
                }
            }

            return stream;
        }
    }
}
