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

import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.IO;

import java.io.IOException;
import java.io.InputStream;

public class StreamBuilderTest {

    @Test
    public void testInclude() throws Exception {

    }

    @Test
    public void testInclude1() throws Exception {

    }

    @Test
    public void testInclude2() throws Exception {

    }

    @Test
    public void testExclude() throws Exception {

    }

    @Test
    public void testExclude1() throws Exception {

    }

    @Test
    public void testExclude2() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testDeleteBetween() throws Exception {
        final String largeHtml = StreamAsserts.getLargeHtml();

        final InputStream in = new StreamBuilder(IO.read(largeHtml)).deleteBetween("<div", ">").get();

        final String actual = IO.slurp(in).replace("<", "\n<");
        final String expected = largeHtml.replaceAll("<div[^>]+>", "<div>").replace("<", "\n<");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteBetween2() throws Exception {
        final String largeHtml = "<td >one</td><td class='red'>two</td>";
        final String expected = "<td>one</td><td>two</td>";

        final InputStream in = new StreamBuilder(IO.read(largeHtml)).deleteBetween("<td", ">").get();
        final String actual = IO.slurp(in);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteBetween3() throws Exception {
        final String largeHtml = "<td >one</td><td class='red'>two</td>";
        final String expected = "two</td>";

        final InputStream in = new StreamBuilder(IO.read(largeHtml))
                .deleteBetween("", "two")
                .get();

        final String actual = IO.slurp(in);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceBeginEnd() throws IOException {
        final String largeHtml = "{FOO}abc";
        final String expected = "appleabc";

        final InputStream in = new StreamBuilder(IO.read(largeHtml))
                .replace("{", "}", "apple")
                .get();

        final String actual = IO.slurp(in);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteBetween1() throws Exception {

    }

    @Test
    public void testReplace() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }
}
