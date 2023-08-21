/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.csv;
import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.CRLF;
import static org.apache.commons.csv.Constants.LF;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
/**
 * CSVParserTest
 *
 * The test are organized in three different sections:
 * The 'setter/getter' section, the lexer section and finally the parser
 * section. In case a test fails, you should follow a top-down approach for
 * fixing a potential bug (its likely that the parser itself fails if the lexer
 * has problems...).
 *
 * @version $Id$
 */
public class CSVParserTest {
    private void validateRecordNumbers(final String lineSeparator) throws IOException {
        final CSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c", CSVFormat.DEFAULT.withRecordSeparator(lineSeparator));
        CSVRecord record;
        assertEquals(0, parser.getRecordNumber());
        assertNotNull(record = parser.nextRecord());
        assertEquals(1, record.getRecordNumber());
        assertEquals(1, parser.getRecordNumber());
        assertNotNull(record = parser.nextRecord());
        assertEquals(2, record.getRecordNumber());
        assertEquals(2, parser.getRecordNumber());
        assertNotNull(record = parser.nextRecord());
        assertEquals(3, record.getRecordNumber());
        assertEquals(3, parser.getRecordNumber());
        assertNull(record = parser.nextRecord());
        assertEquals(3, parser.getRecordNumber());
    }
    private void validateLineNumbers(final String lineSeparator) throws IOException {
        final CSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c", CSVFormat.DEFAULT.withRecordSeparator(lineSeparator));
        assertEquals(0, parser.getCurrentLineNumber());
        assertNotNull(parser.nextRecord());
        assertEquals(1, parser.getCurrentLineNumber());
        assertNotNull(parser.nextRecord());
        assertEquals(2, parser.getCurrentLineNumber());
        assertNotNull(parser.nextRecord());
        // Still 2 because the last line is does not have EOL chars
        assertEquals(2, parser.getCurrentLineNumber());
        assertNull(parser.nextRecord());
        // Still 2 because the last line is does not have EOL chars
        assertEquals(2, parser.getCurrentLineNumber());
    }
    @Test
    public void testNoHeaderMap() throws Exception {
        final CSVParser parser = CSVParser.parse("a,b,c\n1,2,3\nx,y,z", CSVFormat.DEFAULT);
        Assert.assertNull(parser.getHeaderMap());
    }
}
