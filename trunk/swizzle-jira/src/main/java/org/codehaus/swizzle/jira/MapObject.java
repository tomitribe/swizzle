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

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @version $Revision$ $Date$
 */
public class MapObject {

    private final SimpleDateFormat format;
    private final Map fields;

    protected MapObject(Map data) {
        fields = new HashMap(data);
        format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
    }

    protected String getString(String key) {
        return (String) fields.get(key);
    }

    protected boolean getBoolean(String key) {
        String value = getString(key);
        return (value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes"));
    }

    protected int getInt(String key) {
        String value = getString(key);
        return Integer.parseInt(value);
    }

    protected void setString(String key, String value) {
        fields.put(key, value);
    }

    protected void setInt(String key, int value) {
        fields.put(key, Integer.toString(value));
    }

    protected void setBoolean(String key, boolean value) {
        fields.put(key, Boolean.toString(value));
    }

    protected void setDate(String key, Date value) {
        fields.put(key, format.format(value));
    }

    protected Date getDate(String key) {
        try {
            return format.parse(getString(key));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    protected Vector getVector(String key) {
        return (Vector) fields.get(key);
    }

    protected void setVector(String key, Vector value) {
        fields.put(key, value);
    }

    protected List toList(Vector vector, Class type) throws Exception {
        List list = new ArrayList(vector.size());

        Constructor constructor = type.getConstructor(new Class[]{Map.class});
        for (int i = 0; i < vector.size(); i++) {
            Map data = (Map) vector.elementAt(i);
            Object object = constructor.newInstance(new Object[]{data});
            list.add(object);
        }

        return list;
    }

    public Hashtable toHashtable() {
        return new Hashtable(fields);
    }

    protected List getMapObjects(String key, Class type) {
        Vector vector = getVector(key);
        try {
            return toList(vector, type);
        } catch (Exception e) {
            return vector;
        }
    }

    protected void setMapObjects(String key, List objects) {
        Vector vector = new Vector();
        for (int i = 0; i < objects.size(); i++) {
            MapObject mapObject = (MapObject) objects.get(i);
            vector.add(mapObject.toHashtable());
        }
        setVector(key, vector);
    }

}