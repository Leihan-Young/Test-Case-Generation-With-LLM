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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import org.junit.Test;
/** 
 * Test case for the PatternOptionBuilder class.
 */
public class PatternOptionBuilderTest
{
    @Test
    public void testExistingFilePattern() throws Exception
    {
        final Options options = PatternOptionBuilder.parsePattern("g<");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] { "-g", "src/test/resources/existing-readable.file" });

        final Object parsedReadableFileStream = line.getOptionObject("g");

        assertNotNull("option g not parsed", parsedReadableFileStream);
        assertTrue("option g not FileInputStream", parsedReadableFileStream instanceof FileInputStream);
    }
}
