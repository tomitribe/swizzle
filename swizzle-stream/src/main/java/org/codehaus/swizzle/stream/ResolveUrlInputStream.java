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
package org.codehaus.swizzle.stream;

import org.codehaus.swizzle.stream.DelimitedTokenReplacementInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResolveUrlInputStream extends DelimitedTokenReplacementInputStream {

    public ResolveUrlInputStream(InputStream in, String begin, String end, URL url) {
        super(in, begin, end, new UrlResolver(begin, end, url), false);
    }

    public static class UrlResolver extends StringTokenHandler {
        private final URL parent;
        private final String begin;
        private final String end;

        public UrlResolver(String begin, String end, URL parent) {
            this.begin = begin;
            this.end = end;
            this.parent = parent;
        }

        public String handleToken(String token) throws IOException {
            String cleanedToken = token.replaceAll("[ \"\']", "");
            URL newURL = new URL(parent, cleanedToken);

            StringBuffer link = new StringBuffer();
            link.append(begin.toLowerCase());
            if (!begin.endsWith("\"")) {
                link.append('\"');
            }

            link.append(newURL.toExternalForm());

            if (!end.startsWith("\"")) {
                link.append('\"');
            }
            link.append(end);

            return link.toString();
        }
    }
}
