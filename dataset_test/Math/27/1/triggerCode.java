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
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/**
 * @version $Id$
 */
public class FractionTest {
    private void assertFraction(int expectedNumerator, int expectedDenominator, Fraction actual) {
        Assert.assertEquals(expectedNumerator, actual.getNumerator());
        Assert.assertEquals(expectedDenominator, actual.getDenominator());
    }
    private void checkIntegerOverflow(double a) {
        try {
            new Fraction(a, 1.0e-12, 1000);
            Assert.fail("an exception should have been thrown");
        } catch (ConvergenceException ce) {
            // expected behavior
        }
    }
    @Test
    public void testMath835() {
        final int numer = Integer.MAX_VALUE / 99;
        final int denom = 1;
        final double percentage = 100 * ((double) numer) / denom;
        final Fraction frac = new Fraction(numer, denom);
        // With the implementation that preceded the fix suggested in MATH-835,
        // this test was failing, due to overflow. Assert.assertEquals(percentage, frac.percentageValue(), Math.ulp(percentage));
    }
}
