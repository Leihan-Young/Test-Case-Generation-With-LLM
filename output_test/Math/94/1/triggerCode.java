/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math.util;
import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.TestUtils;
/**
 * Test cases for the MathUtils class.
 * @version $Revision$ $Date: 2007-08-16 15:36:33 -0500 (Thu, 16 Aug
 *          2007) $
 */
public final class MathUtilsTest extends TestCase {
    /**
     * Exact recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) {
        if ((n == k) || (k == 0)) {
            return 1;
        }
        if ((k == 1) || (k == n - 1)) {
            return n;
        }
        return binomialCoefficient(n - 1, k - 1) + binomialCoefficient(n - 1, k);
    }
    /**
     * Exact direct multiplication implementation to test against
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    private void testAddAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.addAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }
    }
    private void testMulAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.mulAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }
    }
    private void testSubAndCheckLongFailure(long a, long b) {
        try {
            MathUtils.subAndCheck(a, b);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // success
        }

    }
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        assertEquals(0, MathUtils.gcd(0, 0));

        assertEquals(b, MathUtils.gcd(0, b));
        assertEquals(a, MathUtils.gcd(a, 0));
        assertEquals(b, MathUtils.gcd(0, -b));
        assertEquals(a, MathUtils.gcd(-a, 0));

        assertEquals(10, MathUtils.gcd(a, b));
        assertEquals(10, MathUtils.gcd(-a, b));
        assertEquals(10, MathUtils.gcd(a, -b));
        assertEquals(10, MathUtils.gcd(-a, -b));

        assertEquals(1, MathUtils.gcd(a, c));
        assertEquals(1, MathUtils.gcd(-a, c));
        assertEquals(1, MathUtils.gcd(a, -c));
        assertEquals(1, MathUtils.gcd(-a, -c));

        assertEquals(3 * (1<<15), MathUtils.gcd(3 * (1<<20), 9 * (1<<15)));

    }
}
