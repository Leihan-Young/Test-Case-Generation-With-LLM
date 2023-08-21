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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import junit.framework.TestCase;
/**
 * Abstract test case testing common parser features.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class ParserTestCase extends TestCase
{
    private CommandLine parse(CommandLineParser parser, Options opts, String[] args, Properties properties) throws ParseException {
        if (parser instanceof Parser) {
            return ((Parser) parser).parse(opts, args, properties);
        } else if (parser instanceof DefaultParser) {
            return ((DefaultParser) parser).parse(opts, args, properties);
        } else {
            throw new UnsupportedOperationException("Default options not supported by this parser");
        }
    }
    public void testPropertyOptionGroup() throws Exception
    {
        Options opts = new Options();
        
        OptionGroup group1 = new OptionGroup();
        group1.addOption(new Option("a", null));
        group1.addOption(new Option("b", null));
        opts.addOptionGroup(group1);
        
        OptionGroup group2 = new OptionGroup();
        group2.addOption(new Option("x", null));
        group2.addOption(new Option("y", null));
        opts.addOptionGroup(group2);
        
        String[] args = new String[] { "-a" };
        
        Properties properties = new Properties();
        properties.put("b", "true");
        properties.put("x", "true");
        
        CommandLine cmd = parse(parser, opts, args, properties);
        
        assertTrue(cmd.hasOption("a"));
        assertFalse(cmd.hasOption("b"));
        assertTrue(cmd.hasOption("x"));
        assertFalse(cmd.hasOption("y"));
    }
}
