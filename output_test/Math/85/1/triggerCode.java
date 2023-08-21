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
/**
 * Test cases for NormalDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class NormalDistributionTest extends ContinuousDistributionAbstractTest  {
    private void verifyQuantiles() throws Exception {
        NormalDistribution distribution = (NormalDistribution) getDistribution();
        double mu = distribution.getMean();
        double sigma = distribution.getStandardDeviation();
        setCumulativeTestPoints( new double[] {mu - 2 *sigma, mu - sigma, 
                mu, mu + sigma, mu +2 * sigma,  mu +3 * sigma, mu + 4 * sigma,
                mu + 5 * sigma});
        // Quantiles computed using R (same as Mathematica)
        setCumulativeTestValues(new double[] {0.02275013, 0.1586553, 0.5, 0.8413447, 
                0.9772499, 0.9986501, 0.9999683,  0.9999997});
        verifyCumulativeProbabilities();       
    }
    private void checkDensity(double mean, double sd, double[] x, double[] expected) {
        NormalDistribution d = new NormalDistributionImpl(mean, sd);
        for (int i = 0; i < x.length; i++) {
            assertEquals(expected[i], d.density(x[i]), 1e-9);
        }
    }
    public void testMath280() throws MathException {
        NormalDistribution normal = new NormalDistributionImpl(0,1);
        double result = normal.inverseCumulativeProbability(0.9772498680518209);
        assertEquals(2.0, result, 1.0e-12);
    }
}
