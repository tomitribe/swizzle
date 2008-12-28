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
import java.io.InputStream;

/**
 * @version $Revision$ $Date$
 */
public class ReplaceStringInputStream extends FixedTokenReplacementInputStream {

    public ReplaceStringInputStream(InputStream in, String token, String fixedValue) {
        super(in, token, new FixedStringValueTokenHandler(fixedValue));
    }

    public static class FixedStringValueTokenHandler implements StreamTokenHandler {

        private final String value;

        public FixedStringValueTokenHandler(String value) {
            this.value = value;
        }

        public InputStream processToken(String token) {
            return new ByteArrayInputStream(value.getBytes());
        }
    }

}
