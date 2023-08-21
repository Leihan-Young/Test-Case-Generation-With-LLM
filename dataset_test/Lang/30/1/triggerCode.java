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
import java.util.Locale;
import junit.framework.TestCase;
/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Substring methods
 *
 * @author Apache Software Foundation
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Phil Steitz
 * @version $Id$
 */
public class StringUtilsEqualsIndexOfTest extends TestCase {
    private static final String BAR = "bar";
    private static final String CharU20000 = "\uD840\uDC00";
	private static final String CharU20001 = "\uD840\uDC01";
    private static final String CharUSuppCharHigh = "\uDC00";
    private static final String CharUSuppCharLow = "\uD840";
    private static final String FOO = "foo";
    private static final String FOOBAR = "foobar";
    /**
     * See http://java.sun.com/developer/technicalArticles/Intl/Supplementary/
     */
    public void testContainsNone_CharArrayWithSupplementaryChars() {
        assertEquals(false, StringUtils.containsNone(CharU20000 + CharU20001, CharU20000.toCharArray()));
        assertEquals(false, StringUtils.containsNone(CharU20000 + CharU20001, CharU20001.toCharArray()));
        assertEquals(false, StringUtils.containsNone(CharU20000, CharU20000.toCharArray()));
        // Sanity check:
        assertEquals(-1, CharU20000.indexOf(CharU20001));
        assertEquals(0, CharU20000.indexOf(CharU20001.charAt(0)));
        assertEquals(-1, CharU20000.indexOf(CharU20001.charAt(1)));
        // Test:
        assertEquals(true, StringUtils.containsNone(CharU20000, CharU20001.toCharArray()));
        assertEquals(true, StringUtils.containsNone(CharU20001, CharU20000.toCharArray()));
    }
}
