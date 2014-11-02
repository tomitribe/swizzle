/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

public class StreamBuilder {

    private InputStream stream;

    public StreamBuilder(InputStream stream) {
        this.stream = stream;
    }

    public StreamBuilder include(final String begin, final String end) {
        return include(begin, end, true);
    }

    public StreamBuilder include(final String begin, final String end, final boolean caseSensitive) {
        return include(begin, end, caseSensitive, true);
    }

    public StreamBuilder include(final String begin, final String end, final boolean caseSensitive, final boolean retainDelimiters) {
        stream = new IncludeFilterInputStream(stream, begin, end, caseSensitive, retainDelimiters);
        return this;
    }

    public StreamBuilder exclude(final String begin, final String end) {
        return exclude(begin, end, false, true);
    }

    public StreamBuilder exclude(final String begin, final String end, final boolean caseSensitive) {
        return exclude(begin, end, caseSensitive, false);
    }

    public StreamBuilder exclude(final String begin, final String end, final boolean caseSensitive, final boolean retainDelimiters) {
        stream = new ExcludeFilterInputStream(stream, begin, end, caseSensitive, retainDelimiters);
        return this;
    }

    public StreamBuilder delete(final String token) {
        stream = new ReplaceStringInputStream(stream, token, "");
        return this;
    }

    public StreamBuilder deleteBetween(final String begin, final String end, final boolean caseSensitive) {
        return exclude(begin, end, caseSensitive, false);
    }

    public StreamBuilder deleteBetween(final String begin, final String end) {
        stream = new ExcludeFilterInputStream(stream, begin, end, true, true);
        return this;
    }

    public StreamBuilder replace(final String token, final String with) {
        stream = new ReplaceStringInputStream(stream, token, with);
        return this;
    }

    public InputStream get() {
        return stream;
    }
}
