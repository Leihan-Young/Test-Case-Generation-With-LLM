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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;
/**
 *
 *
 * @version $Id$
 */
public class CSVPrinterTest {
    private static final int ITERATIONS_FOR_RANDOM_TEST = 50000;
    private final String recordSeparator = CSVFormat.DEFAULT.getRecordSeparator();
    private static String printable(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (ch <= ' ' || ch >= 128) {
                sb.append("(").append((int) ch).append(")");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    private void doOneRandom(final CSVFormat format) throws Exception {
        final Random r = new Random();

        final int nLines = r.nextInt(4) + 1;
        final int nCol = r.nextInt(3) + 1;
        // nLines=1;nCol=2;
        final String[][] lines = generateLines(nLines, nCol);

        final StringWriter sw = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(sw, format);

        for (int i = 0; i < nLines; i++) {
            // for (int j=0; j<lines[i].length; j++) System.out.println("### VALUE=:" + printable(lines[i][j]));
            printer.printRecord((Object[]) lines[i]);
        }

        printer.flush();
        printer.close();
        final String result = sw.toString();
        // System.out.println("### :" + printable(result));

        final CSVParser parser = CSVParser.parse(result, format);
        final List<CSVRecord> parseResult = parser.getRecords();

        String[][] expected = lines.clone();
        for (int i = 0; i < expected.length; i++) {
            expected[i] = expectNulls(expected[i], format);
        }
        Utils.compare("Printer output :" + printable(result), expected, parseResult);
        parser.close();
    }
    private String[][] generateLines(final int nLines, final int nCol) {
        final String[][] lines = new String[nLines][];
        for (int i = 0; i < nLines; i++) {
            final String[] line = new String[nCol];
            lines[i] = line;
            for (int j = 0; j < nCol; j++) {
                line[j] = randStr();
            }
        }
        return lines;
    }
    private void doRandom(final CSVFormat format, final int iter) throws Exception {
        for (int i = 0; i < iter; i++) {
            doOneRandom(format);
        }
    }
    private String randStr() {
        final Random r = new Random();

        final int sz = r.nextInt(20);
        // sz = r.nextInt(3);
        final char[] buf = new char[sz];
        for (int i = 0; i < sz; i++) {
            // stick in special chars with greater frequency
            char ch;
            final int what = r.nextInt(20);
            switch (what) {
            case 0:
                ch = '\r';
                break;
            case 1:
                ch = '\n';
                break;
            case 2:
                ch = '\t';
                break;
            case 3:
                ch = '\f';
                break;
            case 4:
                ch = ' ';
                break;
            case 5:
                ch = ',';
                break;
            case 6:
                ch = '"';
                break;
            case 7:
                ch = '\'';
                break;
            case 8:
                ch = '\\';
                break;
            default:
                ch = (char) r.nextInt(300);
                break;
            // default: ch = 'a'; break;
            }
            buf[i] = ch;
        }
        return new String(buf);
    }
    private Connection geH2Connection() throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:mem:my_test;", "sa", "");
    }
    private void setUpTable(final Connection connection) throws SQLException {
        final Statement statement = connection.createStatement();
        try {
            statement.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
            statement.execute("insert into TEST values(1, 'r1')");
            statement.execute("insert into TEST values(2, 'r2')");
        } finally {
            statement.close();
        }
    }
    /**
     * Converts an input CSV array into expected output values WRT NULLs. NULL strings are converted to null values
     * because the parser will convert these strings to null.
     */
    private <T> T[] expectNulls(T[] original, CSVFormat csvFormat) {
        T[] fixed = original.clone();
        for (int i = 0; i < fixed.length; i++) {
            if (ObjectUtils.equals(csvFormat.getNullString(), fixed[i])) {
                fixed[i] = null;
            }
        }
        return fixed;
    }
    private String[] toFirstRecordValues(final String expected, CSVFormat format) throws IOException {
        return CSVParser.parse(expected, format).getRecords().get(0).values();
    }
    private CSVPrinter printWithHeaderComments(final StringWriter sw, final Date now, final CSVFormat baseFormat)
            throws IOException {
        CSVFormat format = baseFormat;
        // Use withHeaderComments first to test CSV-145
        format = format.withHeaderComments("Generated by Apache Commons CSV 1.1", now);
        format = format.withCommentMarker('#');
        format = format.withHeader("Col1", "Col2");
        final CSVPrinter csvPrinter = format.print(sw);
        csvPrinter.printRecord("A", "B");
        csvPrinter.printRecord("C", "D");
        csvPrinter.close();
        return csvPrinter;
    }
    @Test
    public void testMySqlNullOutput() throws IOException {
        Object[] s = new String[] { "NULL", null };
        CSVFormat format = CSVFormat.MYSQL.withQuote('"').withNullString("NULL").withQuoteMode(QuoteMode.NON_NUMERIC);
        StringWriter writer = new StringWriter();
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        String expected = "\"NULL\"\tNULL\n";
        assertEquals(expected, writer.toString());
        String[] record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(new Object[2], record0);

        s = new String[] { "\\N", null };
        format = CSVFormat.MYSQL.withNullString("\\N");
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\\\\N\t\\N\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "\\N", "A" };
        format = CSVFormat.MYSQL.withNullString("\\N");
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\\\\N\tA\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "\n", "A" };
        format = CSVFormat.MYSQL.withNullString("\\N");
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\\n\tA\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "", null };
        format = CSVFormat.MYSQL.withNullString("NULL");
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\tNULL\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "", null };
        format = CSVFormat.MYSQL;
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\t\\N\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "\\N", "", "\u000e,\\\r" };
        format = CSVFormat.MYSQL;
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\\\\N\t\t\u000e,\\\\\\r\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "NULL", "\\\r" };
        format = CSVFormat.MYSQL;
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "NULL\t\\\\\\r\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);

        s = new String[] { "\\\r" };
        format = CSVFormat.MYSQL;
        writer = new StringWriter();
        printer = new CSVPrinter(writer, format);
        printer.printRecord(s);
        printer.close();
        expected = "\\\\\\r\n";
        assertEquals(expected, writer.toString());
        record0 = toFirstRecordValues(expected, format);
        assertArrayEquals(expectNulls(s, format), record0);
    }
}
