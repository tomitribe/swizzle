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
public class BasicObject extends MapObject {

    public BasicObject(Map data) {
        super(data);
    }

    /**
     * the id of this constant
     */
    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        setString("id", id);
    }

    /**
     * the name of the constant
     */
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        setString("name", name);
    }

    /**
     * the description of this constant
     */
    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        setString("description", description);
    }

    /**
     * the URL to retrieve the icon of this constant
     */
    public String getIcon() {
        return getString("icon");
    }

    public void setIcon(String icon) {
        setString("icon", icon);
    }

}
