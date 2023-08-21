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
package org.apache.commons.math.stat.descriptive;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test cases for the {@link SummaryStatistics} class.
 *
 * @version $Id$
 */
public class SummaryStatisticsTest {
    private double one = 1;
    private float twoF = 2;
    private long twoL = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double popVar = 0.5;
    private double std = FastMath.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;
    private void verifySummary(SummaryStatistics u, StatisticalSummary s) {
        Assert.assertEquals("N",s.getN(),u.getN());
        TestUtils.assertEquals("sum",s.getSum(),u.getSum(),tolerance);
        TestUtils.assertEquals("var",s.getVariance(),u.getVariance(),tolerance);
        TestUtils.assertEquals("std",s.getStandardDeviation(),u.getStandardDeviation(),tolerance);
        TestUtils.assertEquals("mean",s.getMean(),u.getMean(),tolerance);
        TestUtils.assertEquals("min",s.getMin(),u.getMin(),tolerance);
        TestUtils.assertEquals("max",s.getMax(),u.getMax(),tolerance);
    }
    @Test
    public void testOverrideMeanWithMathClass() throws Exception {
        double[] scores = {1, 2, 3, 4};
        SummaryStatistics stats = new SummaryStatistics();
        stats.setMeanImpl(new Mean()); 
        for(double i : scores) {
          stats.addValue(i);
        }
        Assert.assertEquals((new Mean()).evaluate(scores),stats.getMean(), 0); 
    }
}
