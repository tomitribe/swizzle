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
package org.tomitribe.swizzle.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelimitedTokenReplacementInputStream extends FilteredInputStream {

    private final ScanBuffer beginBuffer;
    private final ScanBuffer endBuffer;
    private InputStream value;
    private final StreamTokenHandler handler;

    public DelimitedTokenReplacementInputStream(InputStream in, String begin, String end, StreamTokenHandler tokenHandler) {
        this(in, begin, end, tokenHandler, true);
    }

    public DelimitedTokenReplacementInputStream(InputStream in, String begin, String end, StreamTokenHandler tokenHandler, boolean caseSensitive) {
        super(in);
        this.handler = tokenHandler;

        beginBuffer = new ScanBuffer(begin, caseSensitive);
        endBuffer = new ScanBuffer(end, caseSensitive);

        strategy = this::fillBeginBuffer;
    }

    private DelimitedTokenReplacementInputStream.StreamReadingStrategy strategy;

    public int read() throws IOException {
        return strategy.read();
    }

    // reading url (looking for end)
    // flushing url
    // regular read (looking for begin)
    interface StreamReadingStrategy {
        int read() throws IOException;
    }

    /**
     * Step 1 is to seek out the begin token, but first we fill
     * up the begin buffer to capacity.
     *
     * We know a match requires exactly this many bytes so don't
     * bother calling match until we've read enough data.
     *
     * We don't care if there are -1s
     *
     * Once we finish with this step, any byte that overflows out
     * of the beginBuffer is real data and can be read.
     *
     * There is a {@link ScanBuffer#flush()} method that resets a
     * ScanBuffer, however we don't need to call it because all
     * previous bytes are guaranteed to be overwritten. This gives
     * us a littel performance boost over previous version of the
     * code that did call flush.
     */
    private int fillBeginBuffer() throws IOException {
        // Fill up the beginBuffer
        // This doubles as resetting the buffer as
        // we know all prior contents are overwritten
        for (int i = 0; i < beginBuffer.size(); i++) {
            int stream = streamRead();
            beginBuffer.append(stream);
        }

        // Check for a match
        if (beginBuffer.match()) {
            // Sometimes we might immediately find one
            strategy = this::fillEndBuffer;
        } else {
            // Usually we need to keep looking
            strategy = this::scanBegin;
        }

        return strategy.read();
    }

    /**
     * We have a fully filled beginBuffer due all work
     * being done by {@link #fillBeginBuffer()}
     *
     * Now we just need to check the buffer for a match
     * each time we write a byte.
     *
     * If we don't have a match, we write the buffered byte
     * that is now "overflowing" and being replaced.
     *
     * If the stream has ended, this method may end up
     * returning many many -1s.  We don't care.  Effectively,
     * this means we'll never leave this state.
     *
     * We do not write begin tokens to the stream, so once
     * a match is found we immediately shift to fillEndBuffer
     * and simply drop the begin token bytes.
     */
    private int scanBegin() throws IOException {

        int buffered = beginBuffer.append(streamRead());

        if (beginBuffer.match()) {
            strategy = this::fillEndBuffer;
        }

        return buffered;
    }

    /**
     * Once reaching this state we know that the begin token
     * has been found.
     *
     * Our goal is to now find the end token.  While we are
     * looking for the end token we need to track all the
     * content in the middle because it will be passed to the
     * StreamTokenHandler.
     *
     * Before we can do any of this, however, we must fill
     * the endBuffer to capacity.  Just like {@link #fillBeginBuffer()}
     * we know there's a specific number of bytes we need, so
     * we don't bother getting fancy until we've done this
     * step.
     */
    private int fillEndBuffer() throws IOException {
        // Fill up the endBuffer
        // This doubles as resetting the buffer as
        // we know all prior contents are overwritten
        for (int i = 0; i < endBuffer.size(); i++) {
            int stream = streamRead();
            endBuffer.append(stream);
        }

        if (endBuffer.match()) {
            endBuffer.flush();
            // Sometimes we immediately find the end token
            // If this happens, there actually won't be a
            // "middle" token and the StreamTokenHandler
            // will get an empty string
            strategy = this::startReplacement;
        } else {
            // Usually we need to keep looking for the end
            // We will have a non-empty "middle" tokon to
            // give the StreamTokenHandler
            strategy = this::scanEnd;
        }

        return strategy.read();
    }

    StringBuilder token = new StringBuilder();

    /**
     * At this stage we are looking for the end token.
     *
     * Everything that comes off the stream at this point
     * is being buffered.  This buffer will grow until
     * we either find the end token or run out of memory.
     *
     * We may never find the end token.
     */
    private int scanEnd() throws IOException {
        token = new StringBuilder();

        while (true) {
            int buffered = endBuffer.append(streamRead());
            if (buffered != -1) {
                token.append((char) buffered);
            }

            if (endBuffer.match()) {
                endBuffer.flush();
                strategy = this::startReplacement;
                return strategy.read();
            }

            if (buffered == -1) {
                strategy = this::endNotFound;
                return strategy.read();
            }
        }
    }

    /**
     * Once we reach this point we've found the end token
     * and we potentially have "middle" token data saved up.
     *
     * We pass the "middle" token to the StreamTokenHandler,
     * then shift over to making it the primary source of bytes.
     *
     * In the event the StreamTokenHandler decides not to give
     * us any InputStream to read from, we consider our job done
     * and move onto the first step, {@link #fillBeginBuffer()}
     */
    private int startReplacement() throws IOException {
        final String token = this.token.toString();
        this.token = null;

        value = handler.processToken(token);

        if (value != null) {
            strategy = this::flushReplacement;
        } else {
            strategy = this::fillBeginBuffer;
        }

        return strategy.read();
    }

    /**
     * At this point we've read the begin and end tokens,
     * passed all the text inbetween those two to our
     * StreamTokenHandler who gave us an InputStream.
     *
     * Now we just read from that till it starts returning
     * us some -1s indicating we're done with this token.
     *
     * Time to move back to the first step, {@link #fillBeginBuffer()}
     * and start all over again.
     */
    private int flushReplacement() throws IOException {
        final int i = value.read();
        if (i != -1) return i;

        strategy = this::fillBeginBuffer;
        return strategy.read();
    }

    /**
     * Well, we tried to find the end token, but that didn't
     * work so well, did it?  We found ourselves reading
     * and reading and reading and eventually hit the end of
     * the stream.
     *
     * We got this far so we didn't run out of memory, yay.
     *
     * Let's do our best to pretend we didn't just buffer a
     * truck-load of data and leave the stream looking untouched.
     *
     * Write the beginBuffer data, then write the buffered
     * "middle" token data, then we're done.
     *
     */
    private int endNotFound() throws IOException {
        strategy = this::drainBeginBuffer;
        return strategy.read();
    }

    /**
     * We don't normally write the begin token, but we never
     * found the end token, so we flush it out.
     *
     * If the user didn't give us a stream with a matching
     * end token, at least we'll give them all the data so
     * they can see what we saw and maybe fix their data
     * or their code.
     */
    private int drainBeginBuffer() throws IOException {
        final int buffered = beginBuffer.append(-1);
        if (buffered != -1) return buffered;

        value = new ByteArrayInputStream(token.toString().getBytes());
        token = null;
        strategy = this::drainTokenBuffer;
        return strategy.read();
    }

    /**
     * We've just finished writing the begin token, so
     * now we write any buffered "middle" token data that
     * came after it in the stream.
     *
     * Once we're done with this step, we're done.
     *
     * We know there's no end token because the stream
     * is over.
     */
    private int drainTokenBuffer() throws IOException {
        final int buffered = value.read();
        if (buffered != -1) return buffered;

        strategy = this::done;
        return strategy.read();
    }

    /**
     * We know the stream is done, so we'll happily let
     * the user get as many -1s as they "need"
     */
    private int done() throws IOException {
        return -1;
    }

    private int streamRead() throws IOException {
        return super.read();
    }
}
