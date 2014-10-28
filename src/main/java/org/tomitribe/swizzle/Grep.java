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

import java.util.ArrayList;

/**
 * @version $Rev$ $Date$
 */
public class Grep {
    private String regex;

    private int context = 0;

    private String contextSeparator = "--";

    public Grep(String regex, int context) {
        this.regex = regex;

        this.context = context;
    }

    public String filter(String content) {
        ArrayList matches = new ArrayList();

        String[] lines = content.split("\n");

        int contextMatch = 0;

        boolean matched = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.matches(regex)) {
                matches.add(line);

                contextMatch = context;
            } else if (contextMatch > 0) {
                matches.add(line);

                contextMatch--;

                matched = true;
            } else if (lookAhead(i, context, lines)) {
                matches.add(line);
            } else {
                if (matched) {
                    matches.add(contextSeparator);
                }
                matched = false;
            }
        }

        if (matches.size() == 0) {
            return "";
        }

        String last = (String) matches.get(matches.size() - 1);

        if (last.equals(contextSeparator)) {
            matches.remove(matches.size() - 1);
        }

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < matches.size(); i++) {
            result.append((String) matches.get(i));

            result.append('\n');
        }

        return result.toString();
    }

    private boolean lookAhead(int i, int context, String[] lines) {
        int end = Math.min(i + context + 1, lines.length);

        for (; i < end; i++) {
            String line = lines[i];

            if (line.matches(regex)) {
                return true;
            }
        }

        return false;
    }
}
