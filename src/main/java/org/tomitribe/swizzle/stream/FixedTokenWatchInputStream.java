/**
 *
 * Copyright 2018 Tomitribe
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

public class FixedTokenWatchInputStream extends FilteredInputStream {

    private final Consumer<String> handler;
    private final String token;
    private final boolean caseSensitive;
    private Strategy strategy;

    public FixedTokenWatchInputStream(final InputStream in, final String token, final Consumer<String> handler) {
        this(in, token, true, handler);
    }

    public FixedTokenWatchInputStream(final InputStream in, final String token, final boolean caseSensitive, final Consumer<String> handler) {
        super(in);
        this.token = token;
        this.caseSensitive = caseSensitive;
        this.handler = handler;
        this.strategy = new Find();
    }

    public FixedTokenWatchInputStream(final InputStream in, final String token, final Runnable handler) {
        this(in, token, s -> handler.run());
    }

    public FixedTokenWatchInputStream(final InputStream in, final String token, final boolean caseSensitive, final Runnable handler) {
        this(in, token, caseSensitive, s -> handler.run());
    }


    public int read() throws IOException {
        return strategy.read();
    }

    private int superRead() throws IOException {
        return super.read();
    }

    private interface Strategy {
        int read() throws IOException;
    }

    private int done() {
        this.strategy = () -> -1;
        return -1;
    }

    private class Find implements Strategy {
        final ScanBuffer tokenBuffer = new ScanBuffer(token, caseSensitive);

        @Override
        public int read() throws IOException {
            int stream = superRead();
            if (stream == -1) done();

            tokenBuffer.append(stream);

            if (tokenBuffer.match()) {
                strategy = new Find();
                handler.accept(tokenBuffer.getScanString());
            }

            return stream;
        }
    }
}
