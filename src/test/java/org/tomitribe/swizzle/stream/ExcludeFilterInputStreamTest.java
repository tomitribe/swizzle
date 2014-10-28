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

import junit.framework.TestCase;
import org.junit.Ignore;
import org.tomitribe.util.IO;

import java.io.InputStream;

public class ExcludeFilterInputStreamTest extends TestCase {

    @Ignore
    public void test() throws Exception {
        final String input = "<table>\n" +
                "                    <tbody>\n" +
                "                    <tr>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 65px;\">\n" +
                "                        <div class=\"v-table-resizer\"></div>\n" +
                "                        <div class=\"v-table-sort-indicator\"></div>\n" +
                "                        <div class=\"v-table-caption-container v-table-caption-container-align-left\" style=\"width: 49px;\">status</div>\n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 68px;\">\n" +
                "                        <div class=\"v-table-resizer\"></div>\n" +
                "                        <div class=\"v-table-sort-indicator\"></div>\n" +
                "                        <div class=\"v-table-caption-container v-table-caption-container-align-left\" style=\"width: 52px;\">secure</div>\n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 56px;\">\n" +
                "                        <div class=\"v-table-resizer\"></div>\n" +
                "                        <div class=\"v-table-sort-indicator\"></div>\n" +
                "                        <div class=\"v-table-caption-container v-table-caption-container-align-left\" style=\"width: 40px;\">verb</div>\n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 326px;\">\n" +
                "                        <div class=\"v-table-resizer\"></div>\n" +
                "                        <div class=\"v-table-sort-indicator\"></div>\n" +
                "                        <div class=\"v-table-caption-container v-table-caption-container-align-left\" style=\"width: 310px;\">path</div>\n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 587px;\">\n" +
                "                        <div class=\"v-table-resizer\"></div>\n" +
                "                        <div class=\"v-table-sort-indicator\"></div>\n" +
                "                        <div class=\"v-table-caption-container v-table-caption-container-align-left\" style=\"width: 571px;\">summary</div>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                    </tbody>\n" +
                "                  </table>";

        final InputStream in = new ExcludeFilterInputStream(IO.read(input), "<div", "</div>");

        final String output = IO.slurp(in);

        assertEquals("<table>\n" +
                "                    <tbody>\n" +
                "                    <tr>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 65px;\">\n" +
                "                        \n" +
                "                        \n" +
                "                        \n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 68px;\">\n" +
                "                        \n" +
                "                        \n" +
                "                        \n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 56px;\">\n" +
                "                        \n" +
                "                        \n" +
                "                        \n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 326px;\">\n" +
                "                        \n" +
                "                        \n" +
                "                        \n" +
                "                      </td>\n" +
                "                      <td class=\"v-table-header-cell\" style=\"width: 587px;\">\n" +
                "                        \n" +
                "                        \n" +
                "                        \n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                    </tbody>\n" +
                "                  </table>", output);

    }


}
