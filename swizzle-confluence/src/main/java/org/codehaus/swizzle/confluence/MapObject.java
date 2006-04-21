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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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

    public Hashtable toHashtable() {
        return new Hashtable(fields);
    }


    public String toString() {
        return toHashtable().toString();
    }
}
