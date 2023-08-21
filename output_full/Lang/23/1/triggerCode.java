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
package org.apache.commons.lang3.text;
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
import junit.framework.TestCase;
import org.apache.commons.lang3.SystemUtils;
/**
 * Test case for {@link ExtendedMessageFormat}.
 *
 * @since 2.4
 * @version $Id$
 */
public class ExtendedMessageFormatTest extends TestCase {
    private final Map<String, FormatFactory> registry = new HashMap<String, FormatFactory>();
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
    private void checkBuiltInFormat(String pattern, Map<String, ?> registry, Object[] args, Locale[] locales) {
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
    private void checkBuiltInFormat(String pattern, Map<String, ?> registry, Object[] args, Locale locale) {
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
     * Test equals() and hashcode.
     */
    public void testEqualsHashcode() {
        Map<String, ? extends FormatFactory> registry = Collections.singletonMap("testfmt", new LowerCaseFormatFactory());
        Map<String, ? extends FormatFactory> otherRegitry = Collections.singletonMap("testfmt", new UpperCaseFormatFactory());

        String pattern = "Pattern: {0,testfmt}";
        ExtendedMessageFormat emf = new ExtendedMessageFormat(pattern, Locale.US, registry);

        ExtendedMessageFormat other = null;

        // Same object
        assertTrue("same, equals()",   emf.equals(emf));
        assertTrue("same, hashcode()", emf.hashCode() == emf.hashCode());

        // Equal Object
        other = new ExtendedMessageFormat(pattern, Locale.US, registry);
        assertTrue("equal, equals()",   emf.equals(other));
        assertTrue("equal, hashcode()", emf.hashCode() == other.hashCode());

        // Different Class
        other = new OtherExtendedMessageFormat(pattern, Locale.US, registry);
        assertFalse("class, equals()",  emf.equals(other));
        assertTrue("class, hashcode()", emf.hashCode() == other.hashCode()); // same hashcode
        
        // Different pattern
        other = new ExtendedMessageFormat("X" + pattern, Locale.US, registry);
        assertFalse("pattern, equals()",   emf.equals(other));
        assertFalse("pattern, hashcode()", emf.hashCode() == other.hashCode());

        // Different registry
        other = new ExtendedMessageFormat(pattern, Locale.US, otherRegitry);
        assertFalse("registry, equals()",   emf.equals(other));
        assertFalse("registry, hashcode()", emf.hashCode() == other.hashCode());

        // Different Locale
        other = new ExtendedMessageFormat(pattern, Locale.FRANCE, registry);
        assertFalse("locale, equals()",  emf.equals(other));
        assertTrue("locale, hashcode()", emf.hashCode() == other.hashCode()); // same hashcode
    }
}
