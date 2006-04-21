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

import org.codehaus.swizzle.rss.NewsGrabber;
import org.codehaus.swizzle.stream.IncludeFilterInputStream;
import org.codehaus.swizzle.stream.ExcludeFilterInputStream;
import org.codehaus.swizzle.stream.ResolveUrlInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class OReillyNewsGrabber implements NewsGrabber {
    public String getContent(String link) throws IOException {
        System.out.println("link " + link);
        URL url = new URL(link);
        InputStream in = new BufferedInputStream(url.openStream());
        in = new IncludeFilterInputStream(in, "/lpt", ">");
        StringBuffer text = new StringBuffer();

        int b;
        try {
            while ((b = in.read()) != -1 && b != '\"') {
                text.append((char) b);
            }
        } finally {
            in.close();
        }

        url = new URL(url, "/lpt" + text);
        text = new StringBuffer();

//        text.append(url.toExternalForm()+"</br>");
//        text.append(absoluteURL+"</br>");
//        text.append(relativeURL+"</br>");

        in = new BufferedInputStream(url.openStream());

        in = new IncludeFilterInputStream(in, "<HTML>", "</HTML>");
        in = new ExcludeFilterInputStream(in, "<HEAD", "/HEAD>");
        in = new ExcludeFilterInputStream(in, "<BODY", ">");
        in = new ExcludeFilterInputStream(in, "</BODY", ">");
        in = new ExcludeFilterInputStream(in, "<!--", "-->");
        in = new ExcludeFilterInputStream(in, "<SCRIPT", "</SCRIPT>");
        in = new ExcludeFilterInputStream(in, "<NOSCRIPT", "</NOSCRIPT>");
        in = new ExcludeFilterInputStream(in, "<IFRAME", "</IFRAME>");
        in = new ResolveUrlInputStream(in, "<A HREF=", ">", url);
        in = new ResolveUrlInputStream(in, "SRC=\"", "\"", url);

        try {
            while ((b = in.read()) != -1) {
                //System.out.print((char) b);
                text.append((char) b);
            }
            //System.out.println("\n\n\nDONE\n\n\n");
        } finally {
            in.close();
        }
        return text.toString();
    }
}