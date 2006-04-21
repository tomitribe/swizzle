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
package org.codehaus.swizzle;

import java.io.DataInputStream;
import java.io.InputStream;

public class StreamLexer {

    final int BEGIN = 0;
    final int END = 1;

    DataInputStream in;

    String[][] token;

    public StreamLexer(InputStream input, String[][] token) {
        this(new DataInputStream(input), token);
    }

    public StreamLexer(DataInputStream in, String[][] token) {
        this.in = in;
        this.token = token;
    }

    int tokenCount;
    String line;

    int pos;
    int[] buffer = new int[20];

    boolean off;

    public int read() throws Exception {
////        pos = (pos < buffer.length-1)? pos++: 0;
////        buffer[pos] = in.read();
//        if (buffer[pos] == '<' ){

//        }

//        return buffer[pos];
//     public int read() throws IOException {
        int b = in.read();

        if (b == '<') {
            // The cosole has a reference
            // to this input stream
            readToken();
            // Call read recursively as
            // the next character could
            // also be a command
            b = this.read();
        }

        //System.out.println("B="+b);
        return b;
    }

    int depth = 0;

    private void readToken() throws Exception {
        StringBuffer token = new StringBuffer();
        int b = in.read();
        while (true) {
            if (b == '>' && --depth < 1) {
                break;
            } else if (b == '<') {
                depth++;
                token.append((char) b);
            } else {
                token.append((char) b);
            }
            b = in.read();
        }
        System.out.println(token.toString());
    }

    public String _nextToken() throws Exception {
        if (tokenCount >= token.length) {
            tokenCount = 0;
        }
        String begin = token[tokenCount][BEGIN];
        String end = token[tokenCount][END];
        int pos;

        // pickup where we left off
        for (; line != null;) {
            pos = line.indexOf(begin);
            if (pos == -1) break;
            line = line.substring(pos + begin.length());

            pos = line.indexOf(end);
            if (pos == -1) break;
            tokenCount++;
            return line.substring(0, pos);
        }

        // move ahead
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            pos = line.indexOf(begin);
            if (pos == -1) continue;
            line = line.substring(pos + begin.length());

            pos = line.indexOf(end);
            if (pos == -1) continue;
//            tokenCount++;
            return line.substring(0, pos);
        }
        return null;
    }

}
