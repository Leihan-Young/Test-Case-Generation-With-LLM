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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
/**
 * Unit tests {@link org.apache.commons.lang3.math.NumberUtils}.
 *
 * @version $Id$
 */
public class NumberUtilsTest {
    private boolean checkCreateNumber(final String val) {
        try {
            final Object obj = NumberUtils.createNumber(val);
            if (obj == null) {
                return false;
            }
            return true;
        } catch (final NumberFormatException e) {
            return false;
       }
    }
    @Test
    public void TestLang747() {
        assertEquals(Integer.valueOf(0x8000),      NumberUtils.createNumber("0x8000"));
        assertEquals(Integer.valueOf(0x80000),     NumberUtils.createNumber("0x80000"));
        assertEquals(Integer.valueOf(0x800000),    NumberUtils.createNumber("0x800000"));
        assertEquals(Integer.valueOf(0x8000000),   NumberUtils.createNumber("0x8000000"));
        assertEquals(Integer.valueOf(0x7FFFFFFF),  NumberUtils.createNumber("0x7FFFFFFF"));
        assertEquals(Long.valueOf(0x80000000L),    NumberUtils.createNumber("0x80000000"));
        assertEquals(Long.valueOf(0xFFFFFFFFL),    NumberUtils.createNumber("0xFFFFFFFF"));

        // Leading zero tests
        assertEquals(Integer.valueOf(0x8000000),   NumberUtils.createNumber("0x08000000"));
        assertEquals(Integer.valueOf(0x7FFFFFFF),  NumberUtils.createNumber("0x007FFFFFFF"));
        assertEquals(Long.valueOf(0x80000000L),    NumberUtils.createNumber("0x080000000"));
        assertEquals(Long.valueOf(0xFFFFFFFFL),    NumberUtils.createNumber("0x00FFFFFFFF"));

        assertEquals(Long.valueOf(0x800000000L),        NumberUtils.createNumber("0x800000000"));
        assertEquals(Long.valueOf(0x8000000000L),       NumberUtils.createNumber("0x8000000000"));
        assertEquals(Long.valueOf(0x80000000000L),      NumberUtils.createNumber("0x80000000000"));
        assertEquals(Long.valueOf(0x800000000000L),     NumberUtils.createNumber("0x800000000000"));
        assertEquals(Long.valueOf(0x8000000000000L),    NumberUtils.createNumber("0x8000000000000"));
        assertEquals(Long.valueOf(0x80000000000000L),   NumberUtils.createNumber("0x80000000000000"));
        assertEquals(Long.valueOf(0x800000000000000L),  NumberUtils.createNumber("0x800000000000000"));
        assertEquals(Long.valueOf(0x7FFFFFFFFFFFFFFFL), NumberUtils.createNumber("0x7FFFFFFFFFFFFFFF"));
        // N.B. Cannot use a hex constant such as 0x8000000000000000L here as that is interpreted as a negative long
        assertEquals(new BigInteger("8000000000000000", 16), NumberUtils.createNumber("0x8000000000000000"));
        assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16), NumberUtils.createNumber("0xFFFFFFFFFFFFFFFF"));

        // Leading zero tests
        assertEquals(Long.valueOf(0x80000000000000L),   NumberUtils.createNumber("0x00080000000000000"));
        assertEquals(Long.valueOf(0x800000000000000L),  NumberUtils.createNumber("0x0800000000000000"));
        assertEquals(Long.valueOf(0x7FFFFFFFFFFFFFFFL), NumberUtils.createNumber("0x07FFFFFFFFFFFFFFF"));
        // N.B. Cannot use a hex constant such as 0x8000000000000000L here as that is interpreted as a negative long
        assertEquals(new BigInteger("8000000000000000", 16), NumberUtils.createNumber("0x00008000000000000000"));
        assertEquals(new BigInteger("FFFFFFFFFFFFFFFF", 16), NumberUtils.createNumber("0x0FFFFFFFFFFFFFFFF"));
    }
}
