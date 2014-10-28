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
package org.codehaus.swizzle.rss;

import org.codehaus.swizzle.stream.ExcludeFilterInputStream;
import org.codehaus.swizzle.stream.IncludeFilterInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class OSDNNewsGrabber implements NewsGrabber {
    public String getContent(String link) throws IOException {
        link = link.replaceFirst("article.pl", "print.pl");

        URL url = new URL(link);
        InputStream in = new BufferedInputStream(url.openStream());
        in = new IncludeFilterInputStream(in, "<HTML>", "</HTML>");
        in = new ExcludeFilterInputStream(in, "<HEAD", "/HEAD>");
        in = new ExcludeFilterInputStream(in, "<BODY", ">");
        in = new ExcludeFilterInputStream(in, "</BODY", ">");
        in = new ExcludeFilterInputStream(in, "<!--", "-->");
        in = new ExcludeFilterInputStream(in, "<SCRIPT", "</SCRIPT>");
        in = new ExcludeFilterInputStream(in, "<NOSCRIPT", "</NOSCRIPT>");
        in = new ExcludeFilterInputStream(in, "<IFRAME", "</IFRAME>");
        StringBuffer text = new StringBuffer();

        try {
            int b;
            while ((b = in.read()) != -1) {
                text.append((char) b);
            }
        } finally {
            in.close();
        }
        return text.toString();
    }
}