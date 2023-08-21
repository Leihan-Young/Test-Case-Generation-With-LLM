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
s * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.descriptive;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test cases for the {@link UnivariateStatistic} class.
 * @version $Id$
 */
public abstract class UnivariateStatisticAbstractTest {
    @Test
    public void testEvaluateArraySegmentWeighted() {
        // See if this statistic computes weighted statistics
        // If not, skip this test
        UnivariateStatistic statistic = getUnivariateStatistic();
        if (!(statistic instanceof WeightedEvaluation)) {
            return;
        }
        final WeightedEvaluation stat = (WeightedEvaluation) getUnivariateStatistic();
        final double[] arrayZero = new double[5];
        final double[] weightZero = new double[5];
        System.arraycopy(testArray, 0, arrayZero, 0, 5);
        System.arraycopy(testWeightsArray, 0, weightZero, 0, 5);
        Assert.assertEquals(stat.evaluate(arrayZero, weightZero), stat.evaluate(testArray, testWeightsArray, 0, 5), 0);
        final double[] arrayOne = new double[5];
        final double[] weightOne = new double[5];
        System.arraycopy(testArray, 5, arrayOne, 0, 5);
        System.arraycopy(testWeightsArray, 5, weightOne, 0, 5);
        Assert.assertEquals(stat.evaluate(arrayOne, weightOne), stat.evaluate(testArray, testWeightsArray, 5, 5), 0);
        final double[] arrayEnd = new double[5];
        final double[] weightEnd = new double[5];
        System.arraycopy(testArray, testArray.length - 5, arrayEnd, 0, 5);
        System.arraycopy(testWeightsArray, testArray.length - 5, weightEnd, 0, 5);
        Assert.assertEquals(stat.evaluate(arrayEnd, weightEnd), stat.evaluate(testArray, testWeightsArray, testArray.length - 5, 5), 0);
    }
}
