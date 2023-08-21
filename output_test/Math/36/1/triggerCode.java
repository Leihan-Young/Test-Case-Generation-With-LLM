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
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.util.FastMath;
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
    public void testFloatValueForLargeNumeratorAndDenominator() {
        final BigInteger pow400 = BigInteger.TEN.pow(400);
        final BigInteger pow401 = BigInteger.TEN.pow(401);
        final BigInteger two = new BigInteger("2");
        final BigFraction large = new BigFraction(pow401.add(BigInteger.ONE),
                                                  pow400.multiply(two));

        Assert.assertEquals(5, large.floatValue(), 1e-15);
    }
}
