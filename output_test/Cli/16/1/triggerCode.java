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
import junit.framework.TestCase;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
/**
 * Group options are not added to the command line when child elements are
 * detected. This causes the validation of maximum and minimum to fail.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class BugCLI123Test extends TestCase {
    private Option parentOption;
    private Option childOption1;
    private Option childOption2;
    private Group parentGroup;
    private Group childGroup;
    private Parser parser;
    /**
     * Two options of the child group are specified.
     */
    public void testMultipleChildOptions() throws OptionException {
        CommandLine cl = parser.parse(new String[] { "--child", "test",
                "--sub", "anotherTest" });
        assertTrue("Child option not found", cl.hasOption(childOption1));
        assertEquals("Wrong value for option", "test", cl .getValue(childOption1));
        assertTrue("Sub option not found", cl.hasOption(childOption2));
        assertEquals("Wrong value for sub option", "anotherTest", cl .getValue(childOption2));
        assertTrue("Child group not found", cl.hasOption(childGroup));
    }
}
