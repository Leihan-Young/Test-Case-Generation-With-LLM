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
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Test cases for {@link MultivariateNormalDistribution}.
 */
public class MultivariateNormalDistributionTest {
    /**
     * Test the accuracy of the distribution when calculating densities.
     */
    @Test
    public void testUnivariateDistribution() {
        final double[] mu = { -1.5 };
        final double[][] sigma = { { 1 } };
 
        final MultivariateNormalDistribution multi = new MultivariateNormalDistribution(mu, sigma);

        final NormalDistribution uni = new NormalDistribution(mu[0], sigma[0][0]);
        final Random rng = new Random();
        final int numCases = 100;
        final double tol = Math.ulp(1d);
        for (int i = 0; i < numCases; i++) {
            final double v = rng.nextDouble() * 10 - 5;
            Assert.assertEquals(uni.density(v), multi.density(new double[] { v }), tol);
        }
    }
}
