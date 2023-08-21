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
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import junit.framework.TestCase;
/** 
 * Test case for the HelpFormatter class 
 *
 * @author Slawek Zachcial
 * @author John Keyes ( john at integralsource.com )
 * @author brianegge
 */
public class HelpFormatterTest extends TestCase
{
    private static final String EOL = System.getProperty("line.separator");
    public void testIndentedHeaderAndFooter()
    {
        // related to CLI-207
        Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();
        String header = "  Header1\n  Header2";
        String footer = "  Footer1\n  Footer2";
        StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", header, options, 2, 2, footer, true);

        assertEquals( "usage: foobar" + EOL + "  Header1" + EOL + "  Header2" + EOL + "" + EOL + "  Footer1" + EOL + "  Footer2" + EOL , out.toString());
    }
}
