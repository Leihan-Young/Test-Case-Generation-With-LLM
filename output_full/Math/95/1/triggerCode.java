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
/**
 * Test cases for FDistribution.
 * Extends ContinuousDistributionAbstractTest.  See class javadoc for
 * ContinuousDistributionAbstractTest for details.
 * 
 * @version $Revision$ $Date$
 */
public class FDistributionTest extends ContinuousDistributionAbstractTest {
    public void testSmallDegreesOfFreedom() throws Exception {
        org.apache.commons.math.distribution.FDistributionImpl fd =
            new org.apache.commons.math.distribution.FDistributionImpl(
                1.0, 1.0);
        double p = fd.cumulativeProbability(0.975);
        double x = fd.inverseCumulativeProbability(p);
        assertEquals(0.975, x, 1.0e-5);

        fd.setDenominatorDegreesOfFreedom(2.0);
        p = fd.cumulativeProbability(0.975);
        x = fd.inverseCumulativeProbability(p);
        assertEquals(0.975, x, 1.0e-5);
    }
}
