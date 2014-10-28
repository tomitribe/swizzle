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
package org.tomitribe.swizzle.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

public class StringTemplate {

    private final String template;

    public StringTemplate(String template) {
        this.template = template;
    }

    public String apply(Map context) {
        InputStream in = new ByteArrayInputStream(template.getBytes());
        in = new ReplaceVariablesInputStream(in, "{", "}", context);
        StringWriter stringWriter = new StringWriter(template.length() * 2);

        try {
            int i = in.read();
            while (i != -1) {
                stringWriter.write(i);
                i = in.read();
            }
            stringWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to apply the template '" + template + "'", e);
        }
    }
}
