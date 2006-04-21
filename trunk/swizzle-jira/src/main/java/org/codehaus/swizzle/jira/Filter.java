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
package org.codehaus.swizzle.jira;

import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class Filter extends MapObject {

    public Filter(Map data) {
        super(data);
    }


    /**
     * the id of this filter
     */
    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        setString("id", id);
    }

    /**
     * the name of the filter
     */
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        setString("name", name);
    }

    /**
     * the description of this filter
     */
    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        setString("description", description);
    }

    /**
     * the username of this filter's owner
     */
    public String getAuthor() {
        return getString("author");
    }

    public void setAuthor(String author) {
        setString("author", author);
    }

    /**
     * the id of the project this search relates to (null if the search is across projects)
     */
    public String getProject() {
        return getString("project");
    }

    public void setProject(String project) {
        setString("project", project);
    }

    /**
     * a complete XML representation of this search request - I don't recommend you use this for now, it's complex :)
     */
    public String getXml() {
        return getString("xml");
    }

    public void setXml(String xml) {
        setString("xml", xml);
    }

}
