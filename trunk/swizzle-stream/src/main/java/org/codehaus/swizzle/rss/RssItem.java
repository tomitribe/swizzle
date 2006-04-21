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
package org.codehaus.swizzle.rss;

import java.io.IOException;


public class RssItem {

    private NewsGrabber newsGrabber;

    private final String type, title, link, description;
    private String content;

    public RssItem(String type, String title, String link, String description) {
        this.type = type;
        this.title = title;
        this.link = link;
        this.description = description;
        this.newsGrabber = new OReillyNewsGrabber();
        //this.newsGrabber = new SlashdotNewsGrabber();
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() throws IOException {
        if (content == null) {
            content = newsGrabber.getContent(link);
        }
        return content;
    }

}


