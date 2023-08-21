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
package org.apache.commons.math3.optimization.univariate;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.StepFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/**
 * @version $Id$
 */
public final class BrentOptimizerTest {
    /**
     * Contrived example showing that prior to the resolution of MATH-855,
     * the algorithm, by always returning the last evaluated point, would
     * sometimes not report the best point it had found.
     */
    @Test
    public void testMath855() {
        final double minSin = 3 * Math.PI / 2;
        final double offset = 1e-8;
        final double delta = 1e-7;
        final UnivariateFunction f1 = new Sin();
        final UnivariateFunction f2 = new StepFunction(new double[] { minSin, minSin + offset, minSin + 5 * offset },
                                                       new double[] { 0, -1, 0 });
        final UnivariateFunction f = FunctionUtils.add(f1, f2);
        final UnivariateOptimizer optimizer = new BrentOptimizer(1e-8, 1e-100);
        final UnivariatePointValuePair result
            = optimizer.optimize(200, f, GoalType.MINIMIZE, minSin - 6.789 * delta, minSin + 9.876 * delta);
        final int numEval = optimizer.getEvaluations();

        final double sol = result.getPoint();
        final double expected = 4.712389027602411;

        // System.out.println("min=" + (minSin + offset) + " f=" + f.value(minSin + offset));
        // System.out.println("sol=" + sol + " f=" + f.value(sol));
        // System.out.println("exp=" + expected + " f=" + f.value(expected));

        Assert.assertTrue("Best point not reported", f.value(sol) <= f.value(expected));
    }
}
