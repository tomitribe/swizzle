/**
 *
 * Copyright 2006 David Blevins
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * @version $Revision$ $Date$
 */
public class StreamLexer {

    private final PushbackInputStream in;
    private ReplayInputStream mark;

    public StreamLexer(InputStream in) {
        this.in = new PushbackInputStream(in, 4096 * 2);
    }

    /**
     * Seeks in the stream till it finds the start token, reads into a buffer till it finds the end token, then returns the token (the buffer) as a String.
     * 
     * Given the input stream contained the sequence "123ABC456EFG"
     * 
     * InputStream in ... StreamLexer lexer = new StreamLexer(in); String token = lexer.readToken("3","C"); // returns the string "AB" char character = (char)in.read(); // returns the character '4'
     * 
     * Does not support regular expression matching.
     * 
     * @param begin
     *            start token
     * @param end
     *            end token
     * @return the token inbetween the start and end token or null if the end of the stream was reached
     * @throws Exception
     */
    public String readToken(String begin, String end) throws Exception {
        return read(begin, end);
    }

    /**
     * Seeks in the stream till it finds and has completely read the token, then stops. Useful for seeking up to a certain point in the stream.
     * 
     * Given the input stream contained the sequence "000[A]111[B]222[C]345[D]"
     * 
     * InputStream in ... StreamLexer lexer = new StreamLexer(in); String token = lexer.readToken("222"); // returns the string "222" token = lexer.readToken("[", "]"); // returns the string "C" char
     * character = (char)in.read(); // returns the character '3'
     * 
     * Does not support regular expression matching.
     * 
     * @param string
     *            the token to find in the stream
     * @return the token if found in the stream or null if the stream was reached (i.e. the token was not found)
     * @throws Exception
     */
    public String readToken(String string) throws Exception {
        return read(string);
    }

    public String read(String begin, String end) throws IOException {
        final String[] token = { null };
        InputStream search = new DelimitedTokenReplacementInputStream(input(), begin, end, new StringTokenHandler() {
            public String handleToken(String string) throws IOException {
                token[0] = string;
                return string;
            }
        });

        int i = search.read();
        while (i != -1 && token[0] == null) {
            i = search.read();
        }

        return token[0];
    }

    public String read(String string) throws IOException {
        final String[] token = { null };
        InputStream search = new FixedTokenReplacementInputStream(input(), string, new StringTokenHandler() {
            public String handleToken(String string11) throws IOException {
                token[0] = string11;
                return string11;
            }
        });

        int i = search.read();
        while (i != -1 && token[0] == null) {
            i = search.read();
        }
        return token[0];
    }

    public String seek(String begin, String end) throws IOException {

        mark();

        String value = read(begin, end);

        if (value == null) {
            reset();
        } else {
            unmark();
        }

        return value;
    }

    public String seek(String string) throws IOException {

        mark();

        String value = read(string);

        if (value == null) {
            reset();
        } else {
            unmark();
        }

        return value;
    }

    public String peek(String begin, String end) throws IOException {

        mark();

        String value = read(begin, end);

        reset();

        return value;
    }

    public String peek(String string) throws IOException {

        mark();

        String value = read(string);

        reset();

        return value;
    }

    public InputStream input() {
        return (mark != null) ? mark: in;
    }
    
    public StreamLexer mark() throws IOException {
        return mark(null);
    }

    public void unmark() {
        if (mark == null) return;
        mark.reset();
        mark = null;
    }
    
    public StreamLexer mark(String limit) throws IOException {
        if (limit != null) {
            mark = new ReplayInputStream(new TruncateInputStream(in, limit));
        } else {
            mark = new ReplayInputStream(in);
        }
        return this;
    }

    public void reset() throws IOException {
        if (mark == null) {
            throw new IOException("Stream has not been marked");
        }

        in.unread(mark.getBytesRead());
        unmark();
    }

}
