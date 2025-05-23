/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli2.bug;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
/**
 * http://issues.apache.org/jira/browse/CLI-158
 */
public class BugCLI158Test extends TestCase {
    private Parser createDefaultValueParser(String[] defaults) {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        DefaultOption bOption = obuilder.withShortName("b")
                .withLongName("b")
                .withArgument(abuilder.withName("b")
                        .withMinimum(0)
                        .withMaximum(defaults.length)
                        .withDefaults(Arrays.asList(defaults))
                        .create())
                .create();

        Group options = gbuilder
                .withName("options")
                .withOption(bOption)
                .create();

        Parser parser = new Parser();
        parser.setHelpTrigger("--help");
        parser.setGroup(options);
        return parser;
    }
    public void testSingleOptionSingleArgument() throws Exception {
        Parser parser = createDefaultValueParser(new String[]{"100", "1000"});
        String enteredValue1 = "1";
        String[] args = new String[]{"-b", enteredValue1};
        CommandLine cl = parser.parse(args);
        CommandLine cmd = cl;
        assertNotNull(cmd);
        List b = cmd.getValues("-b");
        assertEquals("[" + enteredValue1 + ", 1000]", b + "");
    }
}
