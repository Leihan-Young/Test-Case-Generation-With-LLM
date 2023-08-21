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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * @author John Keyes (john at integralsource.com)
 * @version $Revision$
 */
public class ParseRequiredTest extends TestCase
{
    private Options _options = null;
    private CommandLineParser parser = new PosixParser();
    public void testReuseOptionsTwice() throws Exception
    {
        Options opts = new Options();
		opts.addOption(OptionBuilder.isRequired().create('v'));

		GnuParser parser = new GnuParser();

        // first parsing
        parser.parse(opts, new String[] { "-v" });

        try
        {
            // second parsing, with the same Options instance and an invalid command line
            parser.parse(opts, new String[0]);
            fail("MissingOptionException not thrown");
        }
        catch (MissingOptionException e)
        {
            // expected
        }
    }
}
