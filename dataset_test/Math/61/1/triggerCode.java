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
package org.apache.commons.math.distribution;
import org.apache.commons.math.MathException;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
/**
 * <code>PoissonDistributionTest</code>
 *
 * @version $Revision$ $Date$
 */
public class PoissonDistributionTest extends IntegerDistributionAbstractTest {
    private static final double DEFAULT_TEST_POISSON_PARAMETER = 4.0;
    private void checkProbability(PoissonDistribution dist, double x) throws Exception {
        double p = dist.cumulativeProbability(x);
        assertFalse("NaN cumulative probability returned for mean = " +
                dist.getMean() + " x = " + x, Double.isNaN(p));
        assertTrue("Zero cum probability returned for mean = " +
                dist.getMean() + " x = " + x, p > 0);
    }
    public void testMean() {
        PoissonDistribution dist;
        try {
            dist = new PoissonDistributionImpl(-1);
            fail("negative mean: NotStrictlyPositiveException expected");
        } catch(NotStrictlyPositiveException ex) {
            // Expected.
        }

        dist = new PoissonDistributionImpl(10.0);
        assertEquals(10.0, dist.getMean(), 0.0);
    }
}
