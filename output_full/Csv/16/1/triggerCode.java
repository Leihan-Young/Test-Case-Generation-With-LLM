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
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
/**
 * CSVParserTest
 *
 * The test are organized in three different sections: The 'setter/getter' section, the lexer section and finally the
 * parser section. In case a test fails, you should follow a top-down approach for fixing a potential bug (its likely
 * that the parser itself fails if the lexer has problems...).
 */
public class CSVParserTest {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String UTF_8_NAME = UTF_8.name();
    private static final String CSV_INPUT_1 = "a,b,c,d";
    private static final String CSV_INPUT_2 = "a,b,1 2";
    private BOMInputStream createBOMInputStream(final String resource) throws IOException {
        final URL url = ClassLoader.getSystemClassLoader().getResource(resource);
        return new BOMInputStream(url.openStream());
    }
    private void validateLineNumbers(final String lineSeparator) throws IOException {
        try (final CSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c",
                CSVFormat.DEFAULT.withRecordSeparator(lineSeparator))) {
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
    }
    private void validateRecordNumbers(final String lineSeparator) throws IOException {
        try (final CSVParser parser = CSVParser.parse("a" + lineSeparator + "b" + lineSeparator + "c",
                CSVFormat.DEFAULT.withRecordSeparator(lineSeparator))) {
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
    }
    private void validateRecordPosition(final String lineSeparator) throws IOException {
        final String nl = lineSeparator; // used as linebreak in values for better distinction

        final String code = "a,b,c" + lineSeparator + "1,2,3" + lineSeparator +
                // to see if recordPosition correctly points to the enclosing quote
                "'A" + nl + "A','B" + nl + "B',CC" + lineSeparator +
                // unicode test... not very relevant while operating on strings instead of bytes, but for
                // completeness...
                "\u00c4,\u00d6,\u00dc" + lineSeparator + "EOF,EOF,EOF";

        final CSVFormat format = CSVFormat.newFormat(',').withQuote('\'').withRecordSeparator(lineSeparator);
        CSVParser parser = CSVParser.parse(code, format);

        CSVRecord record;
        assertEquals(0, parser.getRecordNumber());

        assertNotNull(record = parser.nextRecord());
        assertEquals(1, record.getRecordNumber());
        assertEquals(code.indexOf('a'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        assertEquals(2, record.getRecordNumber());
        assertEquals(code.indexOf('1'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        final long positionRecord3 = record.getCharacterPosition();
        assertEquals(3, record.getRecordNumber());
        assertEquals(code.indexOf("'A"), record.getCharacterPosition());
        assertEquals("A" + lineSeparator + "A", record.get(0));
        assertEquals("B" + lineSeparator + "B", record.get(1));
        assertEquals("CC", record.get(2));

        assertNotNull(record = parser.nextRecord());
        assertEquals(4, record.getRecordNumber());
        assertEquals(code.indexOf('\u00c4'), record.getCharacterPosition());

        assertNotNull(record = parser.nextRecord());
        assertEquals(5, record.getRecordNumber());
        assertEquals(code.indexOf("EOF"), record.getCharacterPosition());

        parser.close();

        // now try to read starting at record 3
        parser = new CSVParser(new StringReader(code.substring((int) positionRecord3)), format, positionRecord3, 3);

        assertNotNull(record = parser.nextRecord());
        assertEquals(3, record.getRecordNumber());
        assertEquals(code.indexOf("'A"), record.getCharacterPosition());
        assertEquals("A" + lineSeparator + "A", record.get(0));
        assertEquals("B" + lineSeparator + "B", record.get(1));
        assertEquals("CC", record.get(2));

        assertNotNull(record = parser.nextRecord());
        assertEquals(4, record.getRecordNumber());
        assertEquals(code.indexOf('\u00c4'), record.getCharacterPosition());
        assertEquals("\u00c4", record.get(0));

        parser.close();
    }
    @Test
    public void testIteratorSequenceBreaking() throws IOException {
        final String fiveRows = "1\n2\n3\n4\n5\n";

        // Iterator hasNext() shouldn't break sequence
        CSVParser parser = CSVFormat.DEFAULT.parse(new StringReader(fiveRows));
        int recordNumber = 0;
        Iterator<CSVRecord> iter = parser.iterator();
        recordNumber = 0;
        while (iter.hasNext()) {
            CSVRecord record = iter.next();
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
            if (recordNumber >= 2) {
                break;
            }
        }
        iter.hasNext();
        while (iter.hasNext()) {
            CSVRecord record = iter.next();
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
        }

        // Consecutive enhanced for loops shouldn't break sequence
        parser = CSVFormat.DEFAULT.parse(new StringReader(fiveRows));
        recordNumber = 0;
        for (CSVRecord record : parser) {
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
            if (recordNumber >= 2) {
                break;
            }
        }
        for (CSVRecord record : parser) {
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
        }

        // Consecutive enhanced for loops with hasNext() peeking shouldn't break sequence
        parser = CSVFormat.DEFAULT.parse(new StringReader(fiveRows));
        recordNumber = 0;
        for (CSVRecord record : parser) {
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
            if (recordNumber >= 2) {
                break;
            }
        }
        parser.iterator().hasNext();
        for (CSVRecord record : parser) {
            recordNumber++;
            assertEquals(String.valueOf(recordNumber), record.get(0));
        }
    }
}
