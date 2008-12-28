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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class RssFeed {

    /**
     * URL of the rss news source
     */
    private String feed;
    private Collection rssItems;

    public RssFeed(String feed) throws Exception {
        this.feed = feed;
        rssItems = downloadItems();
    }

    public Collection getItems() throws Exception {
        return rssItems;
    }

    public void refresh() throws Exception {
        downloadItems();
    }

    private Collection downloadItems() throws Exception {
        List items = new Vector();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            marshal(db.parse(feed), items);
        } catch (Exception e) {
            throw new Exception("Unable to process feed " + feed, e);
        }

        return items;
    }

    private void marshal(Node node, List items) throws Exception {
        Node parent = node.getParentNode();
        String name = node.getNodeName();

        if ("link".equals(name) && parent.getNodeName().matches("channel|item")) {
            String type = parent.getNodeName();
            String title = valueOf(node.getPreviousSibling().getPreviousSibling());
            String link = valueOf(node);
            String desc = valueOf(node.getNextSibling().getNextSibling());
            items.add(new RssItem(type, title, link, desc));
        }

        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            marshal(nodes.item(i), items);
        }
    }

    private String valueOf(Node node) throws Exception {
        return ((Text) node.getFirstChild()).getData();
    }
}
