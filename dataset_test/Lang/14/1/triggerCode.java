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
import org.hamcrest.core.IsNot;
import static org.junit.Assert.assertThat;
/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Substring methods
 *
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
    public void testEquals() {
        final CharSequence fooCs = FOO, barCs = BAR, foobarCs = FOOBAR;
        assertTrue(StringUtils.equals(null, null));
        assertTrue(StringUtils.equals(fooCs, fooCs));
        assertTrue(StringUtils.equals(fooCs, (CharSequence) new StringBuilder(FOO)));
        assertTrue(StringUtils.equals(fooCs, (CharSequence) new String(new char[] { 'f', 'o', 'o' })));
        assertTrue(StringUtils.equals(fooCs, (CharSequence) new CustomCharSequence(FOO)));
        assertTrue(StringUtils.equals((CharSequence) new CustomCharSequence(FOO), fooCs));
        assertFalse(StringUtils.equals(fooCs, (CharSequence) new String(new char[] { 'f', 'O', 'O' })));
        assertFalse(StringUtils.equals(fooCs, barCs));
        assertFalse(StringUtils.equals(fooCs, null));
        assertFalse(StringUtils.equals(null, fooCs));
        assertFalse(StringUtils.equals(fooCs, foobarCs));
        assertFalse(StringUtils.equals(foobarCs, fooCs));
    }
}
