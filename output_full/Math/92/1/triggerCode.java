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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Exact (caching) recursive implementation to test against
     */
    private long binomialCoefficient(int n, int k) throws ArithmeticException {
        if (binomialCache.size() > n) {
            Long cachedResult = binomialCache.get(n).get(new Integer(k));
            if (cachedResult != null) {
                return cachedResult.longValue();
            }
        }
        long result = -1;
        if ((n == k) || (k == 0)) {
            result = 1;
        } else if ((k == 1) || (k == n - 1)) {
            result = n;
        } else {
            result = MathUtils.addAndCheck(binomialCoefficient(n - 1, k - 1),
                binomialCoefficient(n - 1, k));
        }
        if (result == -1) {
            throw new ArithmeticException(
                "error computing binomial coefficient");
        }
        for (int i = binomialCache.size(); i < n + 1; i++) {
            binomialCache.add(new HashMap<Integer, Long>());
        }
        binomialCache.get(n).put(new Integer(k), new Long(result));
        return result;
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
    /**
     * Tests correctness for large n and sharpness of upper bound in API doc
     * JIRA: MATH-241
     */
    public void testBinomialCoefficientLarge() throws Exception {
        // This tests all legal and illegal values for n <= 200.
        for (int n = 0; n <= 200; n++) {
            for (int k = 0; k <= n; k++) {
                long ourResult = -1;
                long exactResult = -1;
                boolean shouldThrow = false;
                boolean didThrow = false;
                try {
                    ourResult = MathUtils.binomialCoefficient(n, k);
                } catch (ArithmeticException ex) {
                    didThrow = true;
                }
                try {
                    exactResult = binomialCoefficient(n, k);
                } catch (ArithmeticException ex) {
                    shouldThrow = true;
                }
                assertEquals(n+","+k, shouldThrow, didThrow);
                assertEquals(n+","+k, exactResult, ourResult);
                assertTrue(n+","+k, (n > 66 || !didThrow));
            }
        }

        long ourResult = MathUtils.binomialCoefficient(300, 3);
        long exactResult = binomialCoefficient(300, 3);
        assertEquals(exactResult, ourResult);

        ourResult = MathUtils.binomialCoefficient(700, 697);
        exactResult = binomialCoefficient(700, 697);
        assertEquals(exactResult, ourResult);

        // This one should throw
        try {
            MathUtils.binomialCoefficient(700, 300);
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // Expected
        }

        // Larger values cannot be computed directly by our
        // test implementation because of stack limitations,
        // so we make little jumps to fill the cache.
        for (int i = 2000; i <= 10000; i += 2000) {
            ourResult = MathUtils.binomialCoefficient(i, 3);
            exactResult = binomialCoefficient(i, 3);
            assertEquals(exactResult, ourResult);
        }

    }
}
