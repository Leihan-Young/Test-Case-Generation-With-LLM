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
import java.util.Properties;
import junit.framework.TestCase;
public class ValueTest extends TestCase
{
    private CommandLine _cl = null;
    private Options opts = new Options();
    public void testPropertyOptionFlags() throws Exception
    {
        Properties properties = new Properties();
        properties.setProperty( "a", "true" );
        properties.setProperty( "c", "yes" );
        properties.setProperty( "e", "1" );

        Parser parser = new PosixParser();

        CommandLine cmd = parser.parse(opts, null, properties);
        assertTrue( cmd.hasOption("a") );
        assertTrue( cmd.hasOption("c") );
        assertTrue( cmd.hasOption("e") );


        properties = new Properties();
        properties.setProperty( "a", "false" );
        properties.setProperty( "c", "no" );
        properties.setProperty( "e", "0" );

        cmd = parser.parse(opts, null, properties);
        assertTrue( !cmd.hasOption("a") );
        assertTrue( !cmd.hasOption("c") );
        assertTrue( cmd.hasOption("e") ); // this option accepts as argument


        properties = new Properties();
        properties.setProperty( "a", "TRUE" );
        properties.setProperty( "c", "nO" );
        properties.setProperty( "e", "TrUe" );

        cmd = parser.parse(opts, null, properties);
        assertTrue( cmd.hasOption("a") );
        assertTrue( !cmd.hasOption("c") );
        assertTrue( cmd.hasOption("e") );

        
        properties = new Properties();
        properties.setProperty( "a", "just a string" );
        properties.setProperty( "e", "" );

        cmd = parser.parse(opts, null, properties);
        assertTrue( !cmd.hasOption("a") );
        assertTrue( !cmd.hasOption("c") );
        assertTrue( cmd.hasOption("e") );
    } 
}
