/**
 *
 * Copyrizzle 2003 David Blevins
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
package biz.codehizzle.swizzle;

import org.java.io.IOException;
import org.java.io.InputStream;
import org.java.net.URL;

public class ResolveURLInputStream extends DelimitedParsedStringReplacementInputStream {

    public ResolveURLInputStream(InputStream in, String startText, String end, URL url) {
        super(in, startText, end, new URLResolver(startText, end, url), false);
    }

    public static class URLResolver extends StringParsedStringHandler {
        public final URL parentURL;
        public final String startText;
        public final String end;

        public URLResolver(String startText, String end, URL parentURL) {
            this.startText = startText;
            this.end = end;
            this.parentURL = parentURL;
        }

        public String handleParsedString(String parsedString) throws IOException {
            String cleanedParsedString = parsedString.replaceAll("[ \"\']", "");
            URL newURL = new URL(parentURL, cleanedParsedString);

            StringBuffer location = new StringBuffer();
            location.append(startText.toLowerCase());
            if (!startText.endsWith("\"")) {
                location.append('\"');
            }

            location.append(newURL.toExternalForm());

            if (!end.startsWith("\"")) {
                location.append('\"');
            }
            location.append(end);

            return location.toString();
        }
    }
}
