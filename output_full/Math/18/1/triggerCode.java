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
package org.apache.commons.math3.optimization.direct;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math3.Retry;
import org.apache.commons.math3.RetryRunner;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
/**
 * Test for {@link CMAESOptimizer}.
 */
public class CMAESOptimizerTest {
    /**
     * @param func Function to optimize.
     * @param startPoint Starting point.
     * @param inSigma Individual input sigma.
     * @param boundaries Upper / lower point limit.
     * @param goal Minimization or maximization.
     * @param lambda Population size used for offspring.
     * @param isActive Covariance update mechanism.
     * @param diagonalOnly Simplified covariance update.
     * @param stopValue Termination criteria for optimization.
     * @param fTol Tolerance relative error on the objective function.
     * @param pointTol Tolerance for checking that the optimum is correct.
     * @param maxEvaluations Maximum number of evaluations.
     * @param expected Expected point / value.
     */
    private void doTest(MultivariateFunction func,
            double[] startPoint,
            double[] inSigma,
            double[][] boundaries,
            GoalType goal,
            int lambda,
            boolean isActive,
            int diagonalOnly, 
            double stopValue,
            double fTol,
            double pointTol,
            int maxEvaluations,
            PointValuePair expected) {
        int dim = startPoint.length;
        // test diagonalOnly = 0 - slow but normally fewer feval#
        CMAESOptimizer optim = new CMAESOptimizer(lambda, inSigma, 30000,
                                                  stopValue, isActive, diagonalOnly,
                                                  0, new MersenneTwister(), false);
        final double[] lB = boundaries == null ? null : boundaries[0];
        final double[] uB = boundaries == null ? null : boundaries[1];
        PointValuePair result = optim.optimize(maxEvaluations, func, goal, startPoint, lB, uB);
        // System.out.println("sol=" + Arrays.toString(result.getPoint()));
        Assert.assertEquals(expected.getValue(), result.getValue(), fTol);
        for (int i = 0; i < dim; i++) {
            Assert.assertEquals(expected.getPoint()[i], result.getPoint()[i], pointTol);
        }
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
    /**
     * Cf. MATH-867
     */
    @Test
    public void testFitAccuracyDependsOnBoundary() {
        final CMAESOptimizer optimizer = new CMAESOptimizer();
        final MultivariateFunction fitnessFunction = new MultivariateFunction() {
                public double value(double[] parameters) {
                    final double target = 11.1;
                    final double error = target - parameters[0];
                    return error * error;
                }
            };

        final double[] start = { 1 };
 
        // No bounds.
        PointValuePair result = optimizer.optimize(100000, fitnessFunction, GoalType.MINIMIZE,
                                                   start);
        final double resNoBound = result.getPoint()[0];

        // Optimum is near the lower bound.
        final double[] lower = { -20 };
        final double[] upper = { 5e16 };
        result = optimizer.optimize(100000, fitnessFunction, GoalType.MINIMIZE,
                                    start, lower, upper);
        final double resNearLo = result.getPoint()[0];

        // Optimum is near the upper bound.
        lower[0] = -5e16;
        upper[0] = 20;
        result = optimizer.optimize(100000, fitnessFunction, GoalType.MINIMIZE,
                                    start, lower, upper);
        final double resNearHi = result.getPoint()[0];

        // System.out.println("resNoBound=" + resNoBound +
        //                    " resNearLo=" + resNearLo +
        //                    " resNearHi=" + resNearHi);

        // The two values currently differ by a substantial amount, indicating that
        // the bounds definition can prevent reaching the optimum.
        Assert.assertEquals(resNoBound, resNearLo, 1e-3);
        Assert.assertEquals(resNoBound, resNearHi, 1e-3);
    }
}
