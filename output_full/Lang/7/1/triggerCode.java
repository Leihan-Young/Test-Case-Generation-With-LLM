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
package org.apache.commons.lang3.math;
import static org.apache.commons.lang3.JavaVersion.JAVA_1_3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
/**
 * Unit tests {@link org.apache.commons.lang3.math.NumberUtils}.
 *
 * @version $Id$
 */
public class NumberUtilsTest {
    private boolean checkCreateNumber(String val) {
        try {
            Object obj = NumberUtils.createNumber(val);
            if (obj == null) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
       }
    }
    @Test
    public void testCreateNumber() {
        // a lot of things can go wrong
        assertEquals("createNumber(String) 1 failed", Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5"));
        assertEquals("createNumber(String) 2 failed", Integer.valueOf("12345"), NumberUtils.createNumber("12345"));
        assertEquals("createNumber(String) 3 failed", Double.valueOf("1234.5"), NumberUtils.createNumber("1234.5D"));
        assertEquals("createNumber(String) 3 failed", Double.valueOf("1234.5"), NumberUtils.createNumber("1234.5d"));
        assertEquals("createNumber(String) 4 failed", Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5F"));
        assertEquals("createNumber(String) 4 failed", Float.valueOf("1234.5"), NumberUtils.createNumber("1234.5f"));
        assertEquals("createNumber(String) 5 failed", Long.valueOf(Integer.MAX_VALUE + 1L), NumberUtils.createNumber("" + (Integer.MAX_VALUE + 1L)));
        assertEquals("createNumber(String) 6 failed", Long.valueOf(12345), NumberUtils.createNumber("12345L"));
        assertEquals("createNumber(String) 6 failed", Long.valueOf(12345), NumberUtils.createNumber("12345l"));
        assertEquals("createNumber(String) 7 failed", Float.valueOf("-1234.5"), NumberUtils.createNumber("-1234.5"));
        assertEquals("createNumber(String) 8 failed", Integer.valueOf("-12345"), NumberUtils.createNumber("-12345"));
        assertTrue("createNumber(String) 9a failed", 0xFADE == NumberUtils.createNumber("0xFADE").intValue());
        assertTrue("createNumber(String) 9b failed", 0xFADE == NumberUtils.createNumber("0Xfade").intValue());
        assertTrue("createNumber(String) 10a failed", -0xFADE == NumberUtils.createNumber("-0xFADE").intValue());
        assertTrue("createNumber(String) 10b failed", -0xFADE == NumberUtils.createNumber("-0Xfade").intValue());
        assertEquals("createNumber(String) 11 failed", Double.valueOf("1.1E200"), NumberUtils.createNumber("1.1E200"));
        assertEquals("createNumber(String) 12 failed", Float.valueOf("1.1E20"), NumberUtils.createNumber("1.1E20"));
        assertEquals("createNumber(String) 13 failed", Double.valueOf("-1.1E200"), NumberUtils.createNumber("-1.1E200"));
        assertEquals("createNumber(String) 14 failed", Double.valueOf("1.1E-200"), NumberUtils.createNumber("1.1E-200"));
        assertEquals("createNumber(null) failed", null, NumberUtils.createNumber(null));
        assertEquals("createNumber(String) failed", new BigInteger("12345678901234567890"), NumberUtils .createNumber("12345678901234567890L"));

        // jdk 1.2 doesn't support this. unsure about jdk 1.2.2
        if (SystemUtils.isJavaVersionAtLeast(JAVA_1_3)) {
            assertEquals("createNumber(String) 15 failed", new BigDecimal("1.1E-700"), NumberUtils .createNumber("1.1E-700F"));
        }
        assertEquals("createNumber(String) 16 failed", Long.valueOf("10" + Integer.MAX_VALUE), NumberUtils .createNumber("10" + Integer.MAX_VALUE + "L"));
        assertEquals("createNumber(String) 17 failed", Long.valueOf("10" + Integer.MAX_VALUE), NumberUtils .createNumber("10" + Integer.MAX_VALUE));
        assertEquals("createNumber(String) 18 failed", new BigInteger("10" + Long.MAX_VALUE), NumberUtils .createNumber("10" + Long.MAX_VALUE));

        // LANG-521
        assertEquals("createNumber(String) LANG-521 failed", Float.valueOf("2."), NumberUtils.createNumber("2."));

        // LANG-638
        assertFalse("createNumber(String) succeeded", checkCreateNumber("1eE"));

        // LANG-693
        assertEquals("createNumber(String) LANG-693 failed", Double.valueOf(Double.MAX_VALUE), NumberUtils .createNumber("" + Double.MAX_VALUE));
        
        // LANG-822
        // ensure that the underlying negative number would create a BigDecimal
        final Number bigNum = NumberUtils.createNumber("-1.1E-700F");
        assertEquals(BigDecimal.class,bigNum.getClass());
        assertNotNull(bigNum);

        // Check that the code fails to create a valid number when preceeded by -- rather than - try { NumberUtils.createNumber("--1.1E-700F");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException nfe) {
            // expected
        }
    }
}
