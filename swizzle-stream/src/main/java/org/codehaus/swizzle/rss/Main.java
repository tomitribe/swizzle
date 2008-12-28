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


public class Main {

    public Main() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new OReillyNewsGrabber().getContent("http://www.oreillynet.com/pub/a/windows/2004/03/23/ie_shells.html"));

    }

    public static void _main(String[] args) throws Exception {
//        RssFeed feed = new RssFeed();
//        //bean.setFeed("http://linux.com/linuxcom.rss");
//        feed.setFeed("http://www.oreillynet.com/meerkat/?_fl=rss10&t=ALL&c=916");
//        //bean.setFeed("http://slashdot.org/slashdot.rss");
//
//        PrintStream out = System.out;
//        Collection items = feed.getItems();
//        for (Iterator iter = items.iterator(); iter.hasNext();) {
//            RssItem item = (RssItem) iter.next();
//
//            if (item.getType().equals("channel")) continue;
//
//            out.print("<li class=\"" + item.getType() + "\">");
//            out.print("<a href=\"" + item.getLink() + "\">");
//            out.print(item.getTitle());
//            out.println("</a></li>");
//            out.println();
//
//            out.println(wrap(item.getDescription()));
//            out.println();
//            //out.println(wrap(item.getContent()));
//            out.println(item.getContent());
//            System.exit(0);
//        }

    }

    private static String wrap(String text) {

        StringBuffer wrapped = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            if (i > 0 && (i % 80) == 0) {
                wrapped.append('\n');
            }
            wrapped.append(text.charAt(i));
        }

        return wrapped.toString();
    }
}
