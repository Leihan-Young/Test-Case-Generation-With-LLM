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
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * Unit tests for {@link LocaleUtils}.
 *
 * @author Chris Hyzer
 * @author Stephen Colebourne
 * @version $Id$
 */
public class LocaleUtilsTest extends TestCase {
    private static final Locale LOCALE_EN = new Locale("en", "");
    private static final Locale LOCALE_EN_US = new Locale("en", "US");
    private static final Locale LOCALE_EN_US_ZZZZ = new Locale("en", "US", "ZZZZ");
    private static final Locale LOCALE_FR = new Locale("fr", "");
    private static final Locale LOCALE_FR_CA = new Locale("fr", "CA");
    private static final Locale LOCALE_QQ = new Locale("qq", "");
    private static final Locale LOCALE_QQ_ZZ = new Locale("qq", "ZZ");
    /**
     * Pass in a valid language, test toLocale.
     *
     * @param language  the language string
     */
    private void assertValidToLocale(String language) {
        Locale locale = LocaleUtils.toLocale(language);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        //country and variant are empty
        assertTrue(locale.getCountry() == null || locale.getCountry().length() == 0);
        assertTrue(locale.getVariant() == null || locale.getVariant().length() == 0);
    }
    /**
     * Pass in a valid language, test toLocale.
     *
     * @param localeString to pass to toLocale()
     * @param language of the resulting Locale
     * @param country of the resulting Locale
     */
    private void assertValidToLocale(String localeString, String language, String country) {
        Locale locale = LocaleUtils.toLocale(localeString);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
        //variant is empty
        assertTrue(locale.getVariant() == null || locale.getVariant().length() == 0);
    }
    /**
     * Pass in a valid language, test toLocale.
     *
     * @param localeString to pass to toLocale()
     * @param language of the resulting Locale
     * @param country of the resulting Locale
     * @param variant of the resulting Locale
     */
    private void assertValidToLocale(
            String localeString, String language, 
            String country, String variant) {
        Locale locale = LocaleUtils.toLocale(localeString);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
        assertEquals(variant, locale.getVariant());
        
    }
    /**
     * Helper method for local lookups.
     *
     * @param locale  the input locale
     * @param defaultLocale  the input default locale
     * @param expected  expected results
     */
    private void assertLocaleLookupList(Locale locale, Locale defaultLocale, Locale[] expected) {
        List localeList = defaultLocale == null ?
                LocaleUtils.localeLookupList(locale) :
                LocaleUtils.localeLookupList(locale, defaultLocale);
        
        assertEquals(expected.length, localeList.size());
        assertEquals(Arrays.asList(expected), localeList);
        assertUnmodifiableCollection(localeList);
    }
    /**
     * Make sure the language by country is correct. It checks that 
     * the LocaleUtils.languagesByCountry(country) call contains the 
     * array of languages passed in. It may contain more due to JVM 
     * variations.
     *
     * @param country
     * @param languages array of languages that should be returned
     */
    private void assertLanguageByCountry(String country, String[] languages) {
        List list = LocaleUtils.languagesByCountry(country);
        List list2 = LocaleUtils.languagesByCountry(country);
        assertNotNull(list);
        assertSame(list, list2);
        //search through langauges
        for (int i = 0; i < languages.length; i++) {
            Iterator iterator = list.iterator();
            boolean found = false;
            // see if it was returned by the set
            while (iterator.hasNext()) {
                Locale locale = (Locale) iterator.next();
                // should have an en empty variant
                assertTrue(locale.getVariant() == null
                        || locale.getVariant().length() == 0);
                assertEquals(country, locale.getCountry());
                if (languages[i].equals(locale.getLanguage())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Cound not find language: " + languages[i]
                        + " for country: " + country);
            }
        }
        assertUnmodifiableCollection(list);
    }
    /**
     * Make sure the country by language is correct. It checks that 
     * the LocaleUtils.countryByLanguage(language) call contains the 
     * array of countries passed in. It may contain more due to JVM 
     * variations.
     *
     *
     * @param language
     * @param countries array of countries that should be returned
     */
    private void assertCountriesByLanguage(String language, String[] countries) {
        List list = LocaleUtils.countriesByLanguage(language);
        List list2 = LocaleUtils.countriesByLanguage(language);
        assertNotNull(list);
        assertSame(list, list2);
        //search through langauges
        for (int i = 0; i < countries.length; i++) {
            Iterator iterator = list.iterator();
            boolean found = false;
            // see if it was returned by the set
            while (iterator.hasNext()) {
                Locale locale = (Locale) iterator.next();
                // should have an en empty variant
                assertTrue(locale.getVariant() == null
                        || locale.getVariant().length() == 0);
                assertEquals(language, locale.getLanguage());
                if (countries[i].equals(locale.getCountry())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Cound not find language: " + countries[i]
                        + " for country: " + language);
            }
        }
        assertUnmodifiableCollection(list);
    }
    /**
     * @param coll  the collection to check
     */
    private static void assertUnmodifiableCollection(Collection coll) {
        try {
            coll.add("Unmodifiable");
            fail();
        } catch (UnsupportedOperationException ex) {}
    }
    /**
     * Tests #LANG-328 - only language+variant
     */
    public void testLang328() {
        assertValidToLocale("fr__POSIX", "fr", "", "POSIX");
    }
}
