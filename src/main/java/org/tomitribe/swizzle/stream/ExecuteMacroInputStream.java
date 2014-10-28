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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Really more of an example than anything else. Sky is the limit.
 *
 * @version $Revision$ $Date$
 */
public class ExecuteMacroInputStream extends DelimitedTokenReplacementInputStream {
    public ExecuteMacroInputStream(InputStream in, String begin, String end, Map macros) {
        super(in, begin, end, new MacroLibrary(macros));
    }

    public static class MacroLibrary implements StreamTokenHandler {
        private final Map macros;

        public MacroLibrary(Map macros) {
            this.macros = macros;
        }

        public InputStream processToken(String token) throws IOException {
            String macroName = token.substring(0, token.indexOf(":"));
            // TODO: Construct the "macro" and use the name value pairs to
            // perform IoC
            StreamTokenHandler macro = (StreamTokenHandler) macros.get(macroName);
            if (macro == null) {
                macro = new UnknownMacro();
            }
            return macro.processToken(token);
        }
    }

    public static class Macro {
        private final String name;
        private final Map args;

        public Macro(String token) throws IOException {
            this.name = token.substring(0, token.indexOf(":"));
            token = token.substring(name.length() + 1);

            // This part could benefit from escaping
            String parameters = token.replace('|', '\n');

            Properties args = new Properties();
            args.load(new ByteArrayInputStream(parameters.getBytes()));
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public Map getArgs() {
            return args;
        }

        public String getArg(String key) {
            return (String) args.get(key);
        }
    }

    public static class UnknownMacro extends StringTokenHandler {
        public String handleToken(String token) throws IOException {
            return "{" + token + "}";
        }
    }

    public static class IncludeFileMacro implements StreamTokenHandler {
        public InputStream processToken(String token) throws IOException {
            Macro macro = new Macro(token);
            String path = macro.getArg("path");
            File file = new File(path);
            return new FileInputStream(file);
        }
    }

    public static class IncludeUrlMacro implements StreamTokenHandler {
        public InputStream processToken(String token) throws IOException {
            Macro macro = new Macro(token);
            String urlString = macro.getArg("url");
            URL url = new URL(urlString);
            return url.openStream();
        }
    }
}
