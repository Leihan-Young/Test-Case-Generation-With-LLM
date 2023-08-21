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
package org.apache.commons.math3.distribution;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test cases for HyperGeometriclDistribution.
 * Extends IntegerDistributionAbstractTest.  See class javadoc for
 * IntegerDistributionAbstractTest for details.
 *
 * @version $Id$
 */
public class HypergeometricDistributionTest extends IntegerDistributionAbstractTest {
    private void testHypergeometricDistributionProbabilities(int populationSize, int sampleSize, int numberOfSucceses, double[][] data) {
        HypergeometricDistribution dist = new HypergeometricDistribution(populationSize, numberOfSucceses, sampleSize);
        for (int i = 0; i < data.length; ++i) {
            int x = (int)data[i][0];
            double pmf = data[i][1];
            double actualPmf = dist.probability(x);
            TestUtils.assertRelativelyEquals("Expected equals for <"+x+"> pmf",pmf, actualPmf, 1.0e-9);

            double cdf = data[i][2];
            double actualCdf = dist.cumulativeProbability(x);
            TestUtils.assertRelativelyEquals("Expected equals for <"+x+"> cdf",cdf, actualCdf, 1.0e-9);

            double cdf1 = data[i][3];
            double actualCdf1 = dist.upperCumulativeProbability(x);
            TestUtils.assertRelativelyEquals("Expected equals for <"+x+"> cdf1",cdf1, actualCdf1, 1.0e-9);
        }
    }
    @Test
    public void testMath1021() {
        final int N = 43130568;
        final int m = 42976365;
        final int n = 50;
        final HypergeometricDistribution dist = new HypergeometricDistribution(N, m, n);

        for (int i = 0; i < 100; i++) {
            final int sample = dist.sample();
            Assert.assertTrue("sample=" + sample, 0 <= sample);
            Assert.assertTrue("sample=" + sample, sample <= n);
        }
    }
}
