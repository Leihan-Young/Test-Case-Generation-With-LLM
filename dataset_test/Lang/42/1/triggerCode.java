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
package org.apache.commons.lang;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * Unit tests for {@link StringEscapeUtils}.
 *
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @version $Id$
 */
public class StringEscapeUtilsTest extends TestCase {
    private final static String FOO = "foo";
    private void assertEscapeJava(String escaped, String original) throws IOException {
        assertEscapeJava(null, escaped, original);
    }
    private void assertEscapeJava(String message, String expected, String original) throws IOException {
        String converted = StringEscapeUtils.escapeJava(original);
        message = "escapeJava(String) failed" + (message == null ? "" : (": " + message));
        assertEquals(message, expected, converted);

        StringWriter writer = new StringWriter();
        StringEscapeUtils.escapeJava(writer, original);
        assertEquals(expected, writer.toString());
    }
    private void assertUnescapeJava(String unescaped, String original) throws IOException {
        assertUnescapeJava(null, unescaped, original);
    }
    private void assertUnescapeJava(String message, String unescaped, String original) throws IOException {
        String expected = unescaped;
        String actual = StringEscapeUtils.unescapeJava(original);

        assertEquals("unescape(String) failed" +
                (message == null ? "" : (": " + message)) +
                ": expected '" + StringEscapeUtils.escapeJava(expected) +
                // we escape this so we can see it in the error message
                "' actual '" + StringEscapeUtils.escapeJava(actual) + "'",
                expected, actual);

        StringWriter writer = new StringWriter();
        StringEscapeUtils.unescapeJava(writer, original);
        assertEquals(unescaped, writer.toString());

    }
    private void checkCsvEscapeWriter(String expected, String value) {
        try {
            StringWriter writer = new StringWriter();
            StringEscapeUtils.escapeCsv(writer, value);
            assertEquals(expected, writer.toString());
        } catch (IOException e) {
            fail("Threw: " + e);
        }
    }
    private void checkCsvUnescapeWriter(String expected, String value) {
        try {
            StringWriter writer = new StringWriter();
            StringEscapeUtils.unescapeCsv(writer, value);
            assertEquals(expected, writer.toString());
        } catch (IOException e) {
            fail("Threw: " + e);
        }
    }
    public void testEscapeHtmlHighUnicode() throws java.io.UnsupportedEncodingException {
        // this is the utf8 representation of the character:
        // COUNTING ROD UNIT DIGIT THREE
        // in unicode
        // codepoint: U+1D362
        byte[] data = new byte[] { (byte)0xF0, (byte)0x9D, (byte)0x8D, (byte)0xA2 };

        String escaped = StringEscapeUtils.escapeHtml( new String(data, "UTF8") );
        String unescaped = StringEscapeUtils.unescapeHtml( escaped );

        assertEquals( "High unicode was not escaped correctly", "&#119650;", escaped);
    }
}
