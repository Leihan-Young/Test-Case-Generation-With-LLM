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
import java.io.File;
import junit.framework.TestCase;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.validation.FileValidator;
/**
 * Test case for http://issues.apache.org/jira/browse/CLI-144 CLI 2 throws
 *
 * <pre>
 * Exception in thread &quot;main&quot; java.lang.ClassCastException: java.io.File cannot be cast to java.lang.String
 * </pre>
 *
 * when there should be no such exception
 *
 * @author David Biesack
 */
public class BugCLI144Test extends TestCase {
	public void testFileValidator() {
		final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();
        DefaultOption fileNameOption = obuilder.withShortName("f")
                .withLongName("file-name").withRequired(true).withDescription(
                        "name of an existing file").withArgument(
                        abuilder.withName("file-name").withValidator(
                                FileValidator.getExistingFileInstance())
                                .create()).create();
        Group options = gbuilder.withName("options").withOption(fileNameOption)
                .create();
        Parser parser = new Parser();
        parser.setHelpTrigger("--help");
        parser.setGroup(options);

        final String fileName = "src/test/org/apache/commons/cli2/bug/BugCLI144Test.java";
        CommandLine cl = parser
                .parseAndHelp(new String[] { "--file-name", fileName });
        assertNotNull(cl);
        assertEquals("Wrong file", new File(fileName), cl.getValue(fileNameOption));
	}
}
