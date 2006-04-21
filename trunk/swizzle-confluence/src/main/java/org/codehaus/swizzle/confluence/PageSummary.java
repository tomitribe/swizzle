/**
 *
 * Copyright 2006 David Blevins
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
package org.codehaus.swizzle.confluence;

import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class PageSummary extends MapObject {

    public PageSummary(Map data) {
        super(data);
    }


    /**
     * the id of the page
     */
    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        setString("id", id);
    }

    /**
     * the key of the space that this page belongs to
     */
    public String getSpace() {
        return getString("space");
    }

    public void setSpace(String space) {
        setString("space", space);
    }

    /**
     * the id of the parent page
     */
    public String getParentId() {
        return getString("parentId");
    }

    public void setParentId(String parentId) {
        setString("parentId", parentId);
    }

    /**
     * the title of the page
     */
    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        setString("title", title);
    }

    /**
     * the url to view this page online
     */
    public String getUrl() {
        return getString("url");
    }

    public void setUrl(String url) {
        setString("url", url);
    }

    /**
     * the number of locks current on this page
     */
    public int getLocks() {
        return getInt("locks");
    }

    public void setLocks(int locks) {
        setInt("locks", locks);
    }

}
