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
package org.apache.commons.math3.fraction;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class BigFractionTest {
    private void assertFraction(int expectedNumerator, int expectedDenominator, BigFraction actual) {
        Assert.assertEquals(expectedNumerator, actual.getNumeratorAsInt());
        Assert.assertEquals(expectedDenominator, actual.getDenominatorAsInt());
    }
    private void assertFraction(long expectedNumerator, long expectedDenominator, BigFraction actual) {
        Assert.assertEquals(expectedNumerator, actual.getNumeratorAsLong());
        Assert.assertEquals(expectedDenominator, actual.getDenominatorAsLong());
    }
    @Test
    public void testDigitLimitConstructor() throws ConvergenceException {
        assertFraction(2, 5, new BigFraction(0.4, 9));
        assertFraction(2, 5, new BigFraction(0.4, 99));
        assertFraction(2, 5, new BigFraction(0.4, 999));

        assertFraction(3, 5, new BigFraction(0.6152, 9));
        assertFraction(8, 13, new BigFraction(0.6152, 99));
        assertFraction(510, 829, new BigFraction(0.6152, 999));
        assertFraction(769, 1250, new BigFraction(0.6152, 9999));
        
        // MATH-996
        assertFraction(1, 2, new BigFraction(0.5000000001, 10));
    }
}
