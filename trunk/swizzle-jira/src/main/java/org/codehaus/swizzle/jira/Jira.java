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

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @version $Revision$ $Date$
 */
public class Jira {
    private final XmlRpcClient client;
    private String token;

    public Jira(String endpoint) throws MalformedURLException {
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        if (! endpoint.endsWith("/rpc/xmlrpc")) {
            endpoint += "/rpc/xmlrpc";
        }

        this.client = new XmlRpcClient(endpoint);
    }

    /**
     * Logs the user into JIRA
     */
    public void login(String username, String password) throws Exception {
        token = (String) call("login", username, password);
    }

    /**
     * remove this token from the list of logged in tokens.
     */
    public boolean logout() throws Exception {
        Boolean value = (Boolean) call("logout");
        return value.booleanValue();
    }

    /**
     * Adds a comment to an issue
     */
    public boolean addComment(String issueKey, String comment) throws Exception {
        Boolean value = (Boolean) call("getComments", issueKey, comment);
        return value.booleanValue();
    }

    /**
     * Creates an issue in JIRA
     */
    public Issue createIssue(Issue issue) throws Exception {
        Hashtable data = (Hashtable) call("createIssue", issue.toHashtable());
        return new Issue(data);
    }

    /**
     * Updates an issue in JIRA from a Hashtable object
     */
    public Issue updateIssue(String issueKey, Issue issue) throws Exception {
        Hashtable data = (Hashtable) call("updateIssue", issueKey, issue.toHashtable());
        return new Issue(data);
    }

    /**
     * List<{@link Comment}>:  Returns all comments associated with the issue
     */
    public List getComments(String issueKey) throws Exception {
        Vector vector = (Vector) call("getComments", issueKey);
        return toList(vector, Comment.class);
    }

    /**
     * List<{@link Component}>:  Returns all components available in the specified project
     */
    public List getComponents(String projectKey) throws Exception {
        Vector vector = (Vector) call("getComponents", projectKey);
        return toList(vector, Component.class);
    }

    /**
     * Gets an issue from a given issue key.
     */
    public Issue getIssue(String issueKey) throws Exception {
        Hashtable data = (Hashtable) call("getIssue", issueKey);
        return new Issue(data);
    }

    /**
     * List<{@link Issue}>:  Executes a saved filter
     */
    public List getIssuesFromFilter(String filterId) throws Exception {
        Vector vector = (Vector) call("getIssuesFromFilter");
        return toList(vector, Issue.class);
    }

    /**
     * List<{@link Issue}>:  Find issues using a free text search
     */
    public List getIssuesFromTextSearch(String searchTerms) throws Exception {
        Vector vector = (Vector) call("getIssuesFromTextSearch", searchTerms);
        return toList(vector, Issue.class);
    }

    /**
     * List<{@link Issue}>:  Find issues using a free text search, limited to certain projects
     */
    public List getIssuesFromTextSearchWithProject(Vector projectKeys, String searchTerms, int maxNumResults) throws Exception {
        Vector vector = (Vector) call("getIssuesFromTextSearchWithProject", projectKeys, searchTerms, new Integer(maxNumResults));
        return toList(vector, Issue.class);
    }

    /**
     * List<{@link IssueType}>:  Returns all visible issue types in the system
     */
    public List getIssueTypes() throws Exception {
        Vector vector = (Vector) call("getIssueTypes");
        return toList(vector, IssueType.class);
    }

    /**
     * List<{@link Priority}>:  Returns all priorities in the system
     */
    public List getPriorities() throws Exception {
        Vector vector = (Vector) call("getPriorities");
        return toList(vector, Priority.class);
    }

    /**
     * List<{@link Project}>:  Returns a list of projects available to the user
     */
    public List getProjects() throws Exception {
        Vector vector = (Vector) call("getProjects");
        return toList(vector, Project.class);
    }

    /**
     * List<{@link Resolution}>:  Returns all resolutions in the system
     */
    public List getResolutions() throws Exception {
        Vector vector = (Vector) call("getResolutions");
        return toList(vector, Resolution.class);
    }

    /**
     * List<{@link Filter}>:  Gets all saved filters available for the currently logged in user
     */
    public List getSavedFilters() throws Exception {
        Vector vector = (Vector) call("getSavedFilters");
        return toList(vector, Filter.class);
    }

    /**
     * Returns the Server information such as baseUrl, version, edition, buildDate, buildNumber.
     */
    public ServerInfo getServerInfo() throws Exception {
        Hashtable data = (Hashtable) call("getServerInfo");
        return new ServerInfo(data);
    }

    /**
     * List<{@link Status}>:  Returns all statuses in the system
     */
    public List getStatuses() throws Exception {
        Vector vector = (Vector) call("getStatuses");
        return toList(vector, Status.class);
    }

    /**
     * List<{@link IssueType}>:  Returns all visible subtask issue types in the system
     *
     * @return list of {@link IssueType}
     */
    public List getSubTaskIssueTypes() throws Exception {
        Vector vector = (Vector) call("getSubTaskIssueTypes");
        return toList(vector, IssueType.class);
    }

    /**
     * Returns a user's information given a username
     */
    public User getUser(String username) throws Exception {
        Hashtable data = (Hashtable) call("getUser", username);
        return new User(data);
    }

    /**
     * List<{@link Version}>:  Returns all versions available in the specified project
     */
    public List getVersions(String projectKey) throws Exception {
        Vector vector = (Vector) call("getVersions", projectKey);
        return toList(vector, Version.class);
    }

    private List toList(Vector vector, Class type) throws Exception {
        List list = new ArrayList(vector.size());

        Constructor constructor = type.getConstructor(new Class[]{Map.class});
        for (int i = 0; i < vector.size(); i++) {
            Map data = (Map) vector.elementAt(i);
            Object object = constructor.newInstance(new Object[]{data});
            list.add(object);
        }

        return list;
    }

    private Object call(String command) throws Exception {
        Object[] args = {};
        return call(command, args);
    }

    private Object call(String command, Object arg1) throws Exception {
        Object[] args = {arg1};
        return call(command, args);
    }

    private Object call(String command, Object arg1, Object arg2) throws Exception {
        Object[] args = {arg1, arg2};
        return call(command, args);
    }

    private Object call(String command, Object arg1, Object arg2, Object arg3) throws Exception {
        Object[] args = {arg1, arg2, arg3};
        return call(command, args);
    }

    private Object call(String command, Object[] args) throws XmlRpcException, IOException {
        Vector vector = new Vector();
        if (token != null) vector.add(token);
        vector.addAll(Arrays.asList(args));
        return client.execute("jira1." + command, vector);
    }
}
