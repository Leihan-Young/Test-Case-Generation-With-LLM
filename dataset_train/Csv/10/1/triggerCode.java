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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.junit.Test;
/**
 *
 *
 * @version $Id$
 */
public class CSVPrinterTest {
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
        final String[][] lines = new String[nLines][];
        for (int i = 0; i < nLines; i++) {
            final String[] line = new String[nCol];
            lines[i] = line;
            for (int j = 0; j < nCol; j++) {
                line[j] = randStr();
            }
        }

        final StringWriter sw = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(sw, format);

        for (int i = 0; i < nLines; i++) {
            // for (int j=0; j<lines[i].length; j++) System.out.println("### VALUE=:" + printable(lines[i][j]));
            printer.printRecord((Object[])lines[i]);
        }

        printer.flush();
        printer.close();
        final String result = sw.toString();
        // System.out.println("### :" + printable(result));

        final CSVParser parser = CSVParser.parse(result, format);
        final List<CSVRecord> parseResult = parser.getRecords();

        Utils.compare("Printer output :" + printable(result), lines, parseResult);
        parser.close();
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
    @Test
    public void testHeader() throws IOException {
        final StringWriter sw = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.withQuoteChar(null)
                .withHeader("C1", "C2", "C3"));
        printer.printRecord("a", "b", "c");
        printer.printRecord("x", "y", "z");
        assertEquals("C1,C2,C3\r\na,b,c\r\nx,y,z\r\n", sw.toString());
        printer.close();
    }
}
