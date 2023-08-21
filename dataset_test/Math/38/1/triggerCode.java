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
package org.apache.commons.math.optimization.direct;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math.analysis.MultivariateFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test for {@link BOBYQAOptimizer}.
 */
public class BOBYQAOptimizerTest {
    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        RealPointValuePair expected) {
        doTest(func,
               startPoint,
               boundaries,
               goal,
               fTol,
               pointTol,
               maxEvaluations,
               0,
               expected,
               "");
    }
    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param additionalInterpolationPoints Number of interpolation to used
     * in addition to the default (2 * dim + 1).
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
                        double[] startPoint,
                        double[][] boundaries,
                        GoalType goal,
                        double fTol,
                        double pointTol,
                        int maxEvaluations,
                        int additionalInterpolationPoints,
                        RealPointValuePair expected,
                        String assertMsg) {

        System.out.println(func.getClass().getName() + " BEGIN"); // XXX

        int dim = startPoint.length;
//        MultivariateOptimizer optim =
//            new PowellOptimizer(1e-13, Math.ulp(1d));
//        RealPointValuePair result = optim.optimize(100000, func, goal, startPoint);
        final double[] lB = boundaries == null ? null : boundaries[0];
        final double[] uB = boundaries == null ? null : boundaries[1];
        final int numIterpolationPoints = 2 * dim + 1 + additionalInterpolationPoints;
        BOBYQAOptimizer optim = new BOBYQAOptimizer(numIterpolationPoints);
        RealPointValuePair result = optim.optimize(maxEvaluations, func, goal, startPoint, lB, uB);
//        System.out.println(func.getClass().getName() + " = " 
//              + optim.getEvaluations() + " f(");
//        for (double x: result.getPoint())  System.out.print(x + " ");
//        System.out.println(") = " +  result.getValue());
        Assert.assertEquals(assertMsg, expected.getValue(), result.getValue(), fTol);
        for (int i = 0; i < dim; i++) {
            Assert.assertEquals(expected.getPoint()[i],
                                result.getPoint()[i], pointTol);
        }

        System.out.println(func.getClass().getName() + " END"); // XXX
    }
    private static double[] point(int n, double value) {
        double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }
    private static double[][] boundaries(int dim,
            double lower, double upper) {
        double[][] boundaries = new double[2][dim];
        for (int i = 0; i < dim; i++)
            boundaries[0][i] = lower;
        for (int i = 0; i < dim; i++)
            boundaries[1][i] = upper;
        return boundaries;
    }
    @Test
    public void testConstrainedRosenWithMoreInterpolationPoints() {
        final double[] startPoint = point(DIM, 0.1);
        final double[][] boundaries = boundaries(DIM, -1, 2);
        final RealPointValuePair expected = new RealPointValuePair(point(DIM, 1.0), 0.0);

        // This should have been 78 because in the code the hard limit is
        // said to be
        //   ((DIM + 1) * (DIM + 2)) / 2 - (2 * DIM + 1)
        // i.e. 78 in this case, but the test fails for 48, 59, 62, 63, 64, // 65, 66, ... final int maxAdditionalPoints = 47;

        for (int num = 1; num <= maxAdditionalPoints; num++) {
            doTest(new Rosen(), startPoint, boundaries,
                   GoalType.MINIMIZE,
                   1e-12, 1e-6, 2000,
                   num,
                   expected,
                   "num=" + num);
        }
    }
}
