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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @version $Revision$ $Date$
 */
public abstract class StringTokenHandler implements StreamTokenHandler {

    public abstract String handleToken(String token) throws IOException;

    public final InputStream processToken(String token) throws IOException {
        String result = handleToken(token);
        result = (result != null) ? result : "null";
        return new ByteArrayInputStream(result.getBytes());
    }

}
