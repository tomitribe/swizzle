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
public class Version extends MapObject {

    public Version(Map data) {
        super(data);
    }


    /**
     * the id of the version
     */
    public String getId() {
        return getString("id");
    }

    public void setId(String id) {
        setString("id", id);
    }

    /**
     * the name of the version
     */
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        setString("name", name);
    }

    /**
     * whether or not this version is released
     */
    public boolean getReleased() {
        return getBoolean("released");
    }

    public void setReleased(boolean released) {
        setBoolean("released", released);
    }

    /**
     * whether or not this version is archived
     */
    public boolean getArchived() {
        return getBoolean("archived");
    }

    public void setArchived(boolean archived) {
        setBoolean("archived", archived);
    }

    public int getSequence() {
        return getInt("sequence");
    }

    public void setSequence(int sequence) {
        setInt("sequence", sequence);
    }
}
