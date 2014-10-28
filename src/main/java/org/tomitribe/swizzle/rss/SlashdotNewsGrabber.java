/**
 *
 * Copyright 2004 David Blevins
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
package org.tomitribe.swizzle.rss;

import org.tomitribe.swizzle.stream.ExcludeFilterInputStream;
import org.tomitribe.swizzle.stream.IncludeFilterInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SlashdotNewsGrabber implements NewsGrabber {
    public String getContent(String link) throws IOException {
        System.out.println(link);

        URL url = new URL(link);
        InputStream in = new BufferedInputStream(url.openStream());
        in = new IncludeFilterInputStream(in, "SIZE=\"4\" COLOR=\"#FFFFFF\">", "<p>");
        in = new ExcludeFilterInputStream(in, "</FONT></TD>", "</TABLE>");
        in = new ExcludeFilterInputStream(in, "<!--", "-->");
        in = new ExcludeFilterInputStream(in, "<TABLE", "</TABLE>");
        StringBuffer text = new StringBuffer();

        int b;
        while ((b = in.read()) != -1) {
            System.out.print((char) b);
            text.append((char) b);
        }
        in.close();
        return text.toString().replaceFirst("</FONT><BR>", "</FONT><BR><BR>").replaceFirst("Posted by", "<br><FONT SIZE=\"2\">Posted by").replaceFirst("M</B><BR>", "M</font></B><BR>");
    }
}