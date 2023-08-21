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
    public void testOptionGroupLong() throws Exception
    {
        OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.withLongOpt("foo").create());
        group.addOption(OptionBuilder.withLongOpt("bar").create());
        
        Options options = new Options();
        options.addOptionGroup(group);
        
        CommandLine cl = parser.parse(options, new String[] { "--bar" });
        
        assertTrue(cl.hasOption("bar"));
        assertEquals("selected option", "bar", group.getSelected());
    }
}
