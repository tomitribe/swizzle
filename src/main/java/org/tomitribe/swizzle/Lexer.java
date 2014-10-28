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
package org.tomitribe.swizzle;

import java.io.DataInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    final int BEGIN = 0;
    final int END = 1;

    DataInputStream in;

    String[][] token;
    Pattern[][] patterns;

    public Lexer(byte[] bytes, String[][] token) {
        this.line = new String(bytes);
        this.patterns = new Pattern[token.length][2];
        for (int i = 0; i < token.length; i++) {
            patterns[i][BEGIN] = Pattern.compile(token[i][BEGIN]);
            patterns[i][END] = Pattern.compile(token[i][END]);
        }
    }

    public Lexer(byte[] bytes, Pattern[][] patterns) {
        this.line = new String(bytes);
        this.patterns = patterns;
    }

    int tokenCount;
    String line;

    public String nextToken() throws Exception {
        if (tokenCount >= patterns.length) {
            tokenCount = 0;
        }

        int pos = 0;
        Matcher matcher = null;
        // pickup where we left off
        for (; line != null; ) {
            matcher = patterns[tokenCount][BEGIN].matcher(line);
            if (matcher.find()) {
                line = line.substring(matcher.end());
            } else {
                break;
            }

            matcher = patterns[tokenCount][END].matcher(line);
            if (matcher.find()) {
                try {
                    return line.substring(0, matcher.start()).trim();
                } finally {
                    tokenCount++;
                    line = line.substring(matcher.end());
                }
            } else {
                return null;
            }
        }
        return null;
    }

    public String find(String patternStr, CharSequence input) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
