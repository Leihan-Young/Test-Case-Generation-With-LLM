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
package org.apache.commons.cli;
import junit.framework.TestCase;
/**
 * This is a collection of tests that test real world applications command lines.
 *
 * <p>
 * The following applications are tested:
 * <ul>
 *   <li>ls</li>
 *   <li>Ant</li>
 *   <li>Groovy</li>
 * </ul>
 * </p>
 *
 * @author John Keyes (john at integralsource.com)
 */
public class ApplicationTest extends TestCase {
    public void testGroovy() throws Exception {
        Options options = new Options();

        options.addOption(
            OptionBuilder.withLongOpt("define").
                withDescription("define a system property").
                hasArg(true).
                withArgName("name=value").
                create('D'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("usage information")
            .withLongOpt("help")
            .create('h'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("debug mode will print out full stack traces")
            .withLongOpt("debug")
            .create('d'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("display the Groovy and JVM versions")
            .withLongOpt("version")
            .create('v'));
        options.addOption(
            OptionBuilder.withArgName("charset")
            .hasArg()
            .withDescription("specify the encoding of the files")
            .withLongOpt("encoding")
            .create('c'));
        options.addOption(
            OptionBuilder.withArgName("script")
            .hasArg()
            .withDescription("specify a command line script")
            .create('e'));
        options.addOption(
            OptionBuilder.withArgName("extension")
            .hasOptionalArg()
            .withDescription("modify files in place; create backup if extension is given (e.g. \'.bak\')")
            .create('i'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("process files line by line using implicit 'line' variable")
            .create('n'));
        options.addOption(
            OptionBuilder.hasArg(false)
            .withDescription("process files line by line and print result (see also -n)")
            .create('p'));
        options.addOption(
            OptionBuilder.withArgName("port")
            .hasOptionalArg()
            .withDescription("listen on a port and process inbound lines")
            .create('l'));
        options.addOption(
            OptionBuilder.withArgName("splitPattern")
            .hasOptionalArg()
            .withDescription("split lines using splitPattern (default '\\s') using implicit 'split' variable")
            .withLongOpt("autosplit")
            .create('a'));

        Parser parser = new PosixParser();
        CommandLine line = parser.parse(options, new String[] { "-e", "println 'hello'" }, true);

        assertTrue(line.hasOption('e'));
        assertEquals("println 'hello'", line.getOptionValue('e'));
    }
}
