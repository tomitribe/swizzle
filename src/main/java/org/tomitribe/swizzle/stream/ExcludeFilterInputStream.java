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

import java.io.InputStream;

public class ExcludeFilterInputStream extends IncludeFilterInputStream {

    public ExcludeFilterInputStream(InputStream in, String begin, String end) {
        this(in, begin, end, true);
    }

    public ExcludeFilterInputStream(InputStream in, String begin, String end, final boolean caseSensitive) {
        this(in, begin, end, caseSensitive, false);
    }

    public ExcludeFilterInputStream(InputStream in, String begin, String end, final boolean caseSensitive, final boolean keepDelimiters) {
        super(in, end, begin, caseSensitive, keepDelimiters);
        state = findEnd;
    }
}
