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
package org.apache.commons.lang3;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;
/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils}.
 *
 * @version $Id$
 */
public class StringUtilsTest {
    private void innerTestSplit(char separator, String sepStr, char noMatch) {
        String msg = "Failed on separator hex(" + Integer.toHexString(separator) +
            "), noMatch hex(" + Integer.toHexString(noMatch) + "), sepStr(" + sepStr + ")";
        
        final String str = "a" + separator + "b" + separator + separator + noMatch + "c";
        String[] res;
        // (str, sepStr)
        res = StringUtils.split(str, sepStr);
        assertEquals(msg, 3, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, noMatch + "c", res[2]);
        
        final String str2 = separator + "a" + separator;
        res = StringUtils.split(str2, sepStr);
        assertEquals(msg, 1, res.length);
        assertEquals(msg, "a", res[0]);

        res = StringUtils.split(str, sepStr, -1);
        assertEquals(msg, 3, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, noMatch + "c", res[2]);
        
        res = StringUtils.split(str, sepStr, 0);
        assertEquals(msg, 3, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, noMatch + "c", res[2]);
        
        res = StringUtils.split(str, sepStr, 1);
        assertEquals(msg, 1, res.length);
        assertEquals(msg, str, res[0]);
        
        res = StringUtils.split(str, sepStr, 2);
        assertEquals(msg, 2, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, str.substring(2), res[1]);
    }
    private void innerTestSplitPreserveAllTokens(char separator, String sepStr, char noMatch) {
        String msg = "Failed on separator hex(" + Integer.toHexString(separator) +
            "), noMatch hex(" + Integer.toHexString(noMatch) + "), sepStr(" + sepStr + ")";
        
        final String str = "a" + separator + "b" + separator + separator + noMatch + "c";
        String[] res;
        // (str, sepStr)
        res = StringUtils.splitPreserveAllTokens(str, sepStr);
        assertEquals(msg, 4, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, "", res[2]);
        assertEquals(msg, noMatch + "c", res[3]);
        
        final String str2 = separator + "a" + separator;
        res = StringUtils.splitPreserveAllTokens(str2, sepStr);
        assertEquals(msg, 3, res.length);
        assertEquals(msg, "", res[0]);
        assertEquals(msg, "a", res[1]);
        assertEquals(msg, "", res[2]);

        res = StringUtils.splitPreserveAllTokens(str, sepStr, -1);
        assertEquals(msg, 4, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, "", res[2]);
        assertEquals(msg, noMatch + "c", res[3]);
        
        res = StringUtils.splitPreserveAllTokens(str, sepStr, 0);
        assertEquals(msg, 4, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, "b", res[1]);
        assertEquals(msg, "", res[2]);
        assertEquals(msg, noMatch + "c", res[3]);
        
        res = StringUtils.splitPreserveAllTokens(str, sepStr, 1);
        assertEquals(msg, 1, res.length);
        assertEquals(msg, str, res[0]);
        
        res = StringUtils.splitPreserveAllTokens(str, sepStr, 2);
        assertEquals(msg, 2, res.length);
        assertEquals(msg, "a", res[0]);
        assertEquals(msg, str.substring(2), res[1]);
    }
    private void assertAbbreviateWithOffset(String expected, int offset, int maxWidth) {
        String abcdefghijklmno = "abcdefghijklmno";
        String message = "abbreviate(String,int,int) failed";
        String actual = StringUtils.abbreviate(abcdefghijklmno, offset, maxWidth);
        if (offset >= 0 && offset < abcdefghijklmno.length()) {
            assertTrue(message + " -- should contain offset character",
                    actual.indexOf((char)('a'+offset)) != -1);
        }
        assertTrue(message + " -- should not be greater than maxWidth",
                actual.length() <= maxWidth);
        assertEquals(message, expected, actual);
    }
    @Test
    public void testEscapeSurrogatePairs() throws Exception {
        assertEquals("\uD83D\uDE30", StringEscapeUtils.escapeCsv("\uD83D\uDE30"));
        // Examples from https://en.wikipedia.org/wiki/UTF-16
        assertEquals("\uD800\uDC00", StringEscapeUtils.escapeCsv("\uD800\uDC00"));
        assertEquals("\uD834\uDD1E", StringEscapeUtils.escapeCsv("\uD834\uDD1E"));
        assertEquals("\uDBFF\uDFFD", StringEscapeUtils.escapeCsv("\uDBFF\uDFFD"));
        
    }
}
