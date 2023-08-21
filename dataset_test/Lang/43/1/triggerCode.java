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
package org.apache.commons.lang.text;
import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang.SystemUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test case for {@link ExtendedMessageFormat}.
 *
 * @since 2.4
 * @version $Id$
 */
public class ExtendedMessageFormatTest extends TestCase {
    private Map registry = new HashMap();
    /**
     * Test a built in format for the specified Locales, plus <code>null</code> Locale.
     * @param pattern MessageFormat pattern
     * @param args MessageFormat arguments
     * @param locales to test
     */
    private void checkBuiltInFormat(String pattern, Object[] args, Locale[] locales) {
        checkBuiltInFormat(pattern, null, args, locales);
    }
    /**
     * Test a built in format for the specified Locales, plus <code>null</code> Locale.
     * @param pattern MessageFormat pattern
     * @param registry FormatFactory registry to use
     * @param args MessageFormat arguments
     * @param locales to test
     */
    private void checkBuiltInFormat(String pattern, Map registry, Object[] args, Locale[] locales) {
        checkBuiltInFormat(pattern, registry, args, (Locale) null);
        for (int i = 0; i < locales.length; i++) {
            checkBuiltInFormat(pattern, registry, args, locales[i]);
        }
    }
    /**
     * Create an ExtendedMessageFormat for the specified pattern and locale and check the
     * formated output matches the expected result for the parameters.
     * @param pattern string
     * @param registry map
     * @param args Object[]
     * @param locale Locale
     */
    private void checkBuiltInFormat(String pattern, Map registry, Object[] args, Locale locale) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Pattern=[");
        buffer.append(pattern);
        buffer.append("], locale=[");
        buffer.append(locale);
        buffer.append("]");
        MessageFormat mf = createMessageFormat(pattern, locale);
        // System.out.println(buffer + ", result=[" + mf.format(args) +"]");
        ExtendedMessageFormat emf = null;
        if (locale == null) {
            emf = new ExtendedMessageFormat(pattern);
        } else {
            emf = new ExtendedMessageFormat(pattern, locale);
        }
        assertEquals("format "    + buffer.toString(), mf.format(args), emf.format(args));
        assertPatternsEqual("toPattern " + buffer.toString(), mf.toPattern(),  emf.toPattern());
    }
    private void assertPatternsEqual(String message, String expected, String actual) {
        if (SystemUtils.isJavaVersionAtLeast(1.4f)) {
            assertEquals(message, expected, actual);
        }
    }
    /**
     * Replace MessageFormat(String, Locale) constructor (not available until JDK 1.4).
     * @param pattern string
     * @param locale Locale
     * @return MessageFormat
     */
    private MessageFormat createMessageFormat(String pattern, Locale locale) {
        MessageFormat result = new MessageFormat(pattern);
        if (locale != null) {
            result.setLocale(locale);
            result.applyPattern(pattern);
        }
        return result;
    }
    /**
     * Test Bug LANG-477 - out of memory error with escaped quote
     */
    public void testEscapedQuote_LANG_477() {
        String pattern = "it''s a {0,lower} 'test'!";
        ExtendedMessageFormat emf = new ExtendedMessageFormat(pattern, registry);
        assertEquals("it's a dummy test!", emf.format(new Object[] {"DUMMY"}));
    }
}
