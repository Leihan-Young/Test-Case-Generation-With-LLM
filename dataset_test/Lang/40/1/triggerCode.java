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
import java.util.Locale;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * Unit tests {@link org.apache.commons.lang.StringUtils} - Substring methods
 *
 * @author <a href="mailto:scolebourne@joda.org">Stephen Colebourne</a>
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Phil Steitz
 * @version $Id$
 */
public class StringUtilsEqualsIndexOfTest extends TestCase {
    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String FOOBAR = "foobar";
    public void testContainsIgnoreCase_LocaleIndependence() {
        Locale orig = Locale.getDefault();

        Locale[] locales = { Locale.ENGLISH, new Locale("tr"), Locale.getDefault() };

        String[][] tdata = { 
            { "i", "I" },
            { "I", "i" },
            { "\u03C2", "\u03C3" },
            { "\u03A3", "\u03C2" },
            { "\u03A3", "\u03C3" },
        };

        String[][] fdata = { 
            { "\u00DF", "SS" },
        };

        try {
            for (int i = 0; i < locales.length; i++) {
                Locale.setDefault(locales[i]);
                for (int j = 0; j < tdata.length; j++) {
                    assertTrue(Locale.getDefault() + ": " + j + " " + tdata[j][0] + " " + tdata[j][1], StringUtils .containsIgnoreCase(tdata[j][0], tdata[j][1]));
                }
                for (int j = 0; j < fdata.length; j++) {
                    assertFalse(Locale.getDefault() + ": " + j + " " + fdata[j][0] + " " + fdata[j][1], StringUtils .containsIgnoreCase(fdata[j][0], fdata[j][1]));
                }
            }
        } finally {
            Locale.setDefault(orig);
        }
    }
}
