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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @version $Revision$ $Date$
 */
public class Issue extends MapObject {

    public Issue() {
        super(new HashMap());
    }

    public Issue(Map data) {
        super(data);
    }

    /**
     *
     */
    public String getProject() {
        return getString("project");
    }

    public void setProject(String project) {
        setString("project", project);
    }

    /**
     *
     */
    public int getType() {
        return getInt("type");
    }

    public void setType(int type) {
        setInt("type", type);
    }

    /**
     * example: 2005-10-11 06:10:39.115
     */
    public Date getCreated() {
        return getDate("created");
    }

    public void setCreated(Date created) {
        setDate("created", created);
    }


    /**
     *
     */
    public String getSummary() {
        return getString("summary");
    }

    public void setSummary(String summary) {
        setString("summary", summary);
    }

    /**
     *
     */
    public int getVotes() {
        return getInt("votes");
    }

    public void setVotes(int votes) {
        setInt("votes", votes);
    }

    /**
     * List of something
     */
    public Vector getCustomFieldValues() {
        return getVector("customFieldValues");
    }

    public void setCustomFieldValues(Vector customFieldValues) {
        setVector("customFieldValues", customFieldValues);
    }

    /**
     * List of Components
     */
    public List getComponents() {
        return getMapObjects("components", Component.class);
    }

    public void setComponents(Vector components) {
        setMapObjects("components", components);
    }

    /**
     * List of Versions
     */
    public List getAffectsVersions() {
        return getMapObjects("affectsVersions", Version.class);
    }

    public void setAffectsVersions(Vector affectsVersions) {
        setMapObjects("affectsVersions", affectsVersions);
    }

    /**
     * 28093
     */
    public int getId() {
        return getInt("id");
    }

    public void setId(int id) {
        setInt("id", id);
    }

    /**
     * 6
     */
    public int getStatus() {
        return getInt("status");
    }

    public void setStatus(int status) {
        setInt("status", status);
    }

    /**
     * 1
     */
    public int getResolution() {
        return getInt("resolution");
    }

    public void setResolution(int resolution) {
        setInt("resolution", resolution);
    }

    /**
     * List
     */
    public List getFixVersions() {
        return getMapObjects("fixVersions", Version.class);
    }

    public void setFixVersions(List fixVersions) {
        setMapObjects("fixVersions", fixVersions);
    }

    /**
     *
     */
    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        setString("description", description);
    }

    /**
     *
     */
    public String getReporter() {
        return getString("reporter");
    }

    public void setReporter(String reporter) {
        setString("reporter", reporter);
    }

    /**
     *
     */
    public Date getUpdated() {
        return getDate("updated");
    }

    public void setUpdated(Date updated) {
        setDate("updated", updated);
    }

    /**
     *
     */
    public String getAssignee() {
        return getString("assignee");
    }

    public void setAssignee(String assignee) {
        setString("assignee", assignee);
    }

    /**
     *
     */
    public int getPriority() {
        return getInt("priority");
    }

    public void setPriority(int priority) {
        setInt("priority", priority);
    }

    /**
     *
     */
    public String getKey() {
        return getString("key");
    }

    public void setKey(String key) {
        setString("key", key);
    }

}
