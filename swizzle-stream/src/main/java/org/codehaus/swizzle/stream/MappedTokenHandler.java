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

package org.codehaus.swizzle.stream;

import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public class MappedTokenHandler extends StringTokenHandler {

    private final Map entries;

    public MappedTokenHandler(Map entries) {
        this.entries = entries;
    }

    public String handleToken(String token) {
        Object object = entries.get(token);
        if (object != null) {
            return object.toString();
        }
        return token;
    }
}
