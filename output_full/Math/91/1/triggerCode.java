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
package org.apache.commons.math.fraction;
import org.apache.commons.math.ConvergenceException;
import junit.framework.TestCase;
/**
 * @version $Revision$ $Date$
 */
public class FractionTest extends TestCase {
    private void assertFraction(int expectedNumerator, int expectedDenominator, Fraction actual) {
        assertEquals(expectedNumerator, actual.getNumerator());
        assertEquals(expectedDenominator, actual.getDenominator());
    }
    private void checkIntegerOverflow(double a) {
        try {
            new Fraction(a, 1.0e-12, 1000);
            fail("an exception should have been thrown");
        } catch (ConvergenceException ce) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }
    }
    public void testCompareTo() {
        Fraction first = new Fraction(1, 2);
        Fraction second = new Fraction(1, 3);
        Fraction third = new Fraction(1, 2);
        
        assertEquals(0, first.compareTo(first));
        assertEquals(0, first.compareTo(third));
        assertEquals(1, first.compareTo(second));
        assertEquals(-1, second.compareTo(first));

        // these two values are different approximations of PI
        // the first  one is approximately PI - 3.07e-18
        // the second one is approximately PI + 1.936e-17
        Fraction pi1 = new Fraction(1068966896, 340262731);
        Fraction pi2 = new Fraction( 411557987, 131002976);
        assertEquals(-1, pi1.compareTo(pi2));
        assertEquals( 1, pi2.compareTo(pi1));
        assertEquals(0.0, pi1.doubleValue() - pi2.doubleValue(), 1.0e-20);
    }
}
