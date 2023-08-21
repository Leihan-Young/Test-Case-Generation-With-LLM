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
package org.apache.commons.math3.fitting;
import java.util.Random;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction.Parametric;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.TestUtils;
import org.junit.Test;
import org.junit.Assert;
/**
 * Test for class {@link CurveFitter} where the function to fit is a
 * polynomial.
 */
public class PolynomialFitterTest {
    /**
     * @param optimizer Optimizer.
     * @param maxEval Maximum number of function evaluations.
     * @param init First guess.
     * @return the solution found by the given optimizer.
     */
    private double[] doMath798(MultivariateVectorOptimizer optimizer,
                               int maxEval,
                               double[] init) {
        final CurveFitter<Parametric> fitter = new CurveFitter<Parametric>(optimizer);

        fitter.addObservedPoint(-0.2, -7.12442E-13);
        fitter.addObservedPoint(-0.199, -4.33397E-13);
        fitter.addObservedPoint(-0.198, -2.823E-13);
        fitter.addObservedPoint(-0.197, -1.40405E-13);
        fitter.addObservedPoint(-0.196, -7.80821E-15);
        fitter.addObservedPoint(-0.195, 6.20484E-14);
        fitter.addObservedPoint(-0.194, 7.24673E-14);
        fitter.addObservedPoint(-0.193, 1.47152E-13);
        fitter.addObservedPoint(-0.192, 1.9629E-13);
        fitter.addObservedPoint(-0.191, 2.12038E-13);
        fitter.addObservedPoint(-0.19, 2.46906E-13);
        fitter.addObservedPoint(-0.189, 2.77495E-13);
        fitter.addObservedPoint(-0.188, 2.51281E-13);
        fitter.addObservedPoint(-0.187, 2.64001E-13);
        fitter.addObservedPoint(-0.186, 2.8882E-13);
        fitter.addObservedPoint(-0.185, 3.13604E-13);
        fitter.addObservedPoint(-0.184, 3.14248E-13);
        fitter.addObservedPoint(-0.183, 3.1172E-13);
        fitter.addObservedPoint(-0.182, 3.12912E-13);
        fitter.addObservedPoint(-0.181, 3.06761E-13);
        fitter.addObservedPoint(-0.18, 2.8559E-13);
        fitter.addObservedPoint(-0.179, 2.86806E-13);
        fitter.addObservedPoint(-0.178, 2.985E-13);
        fitter.addObservedPoint(-0.177, 2.67148E-13);
        fitter.addObservedPoint(-0.176, 2.94173E-13);
        fitter.addObservedPoint(-0.175, 3.27528E-13);
        fitter.addObservedPoint(-0.174, 3.33858E-13);
        fitter.addObservedPoint(-0.173, 2.97511E-13);
        fitter.addObservedPoint(-0.172, 2.8615E-13);
        fitter.addObservedPoint(-0.171, 2.84624E-13);

        final double[] coeff = fitter.fit(maxEval,
                                          new PolynomialFunction.Parametric(),
                                          init);
        return coeff;
    }
    private void checkUnsolvableProblem(MultivariateVectorOptimizer optimizer,
                                        boolean solvable) {
        Random randomizer = new Random(1248788532l);
        for (int degree = 0; degree < 10; ++degree) {
            PolynomialFunction p = buildRandomPolynomial(degree, randomizer);

            PolynomialFitter fitter = new PolynomialFitter(optimizer);

            // reusing the same point over and over again does not bring
            // information, the problem cannot be solved in this case for
            // degrees greater than 1 (but one point is sufficient for
            // degree 0)
            for (double x = -1.0; x < 1.0; x += 0.01) {
                fitter.addObservedPoint(1.0, 0.0, p.value(0.0));
            }

            try {
                final double[] init = new double[degree + 1];
                fitter.fit(init);
                Assert.assertTrue(solvable || (degree == 0));
            } catch(ConvergenceException e) {
                Assert.assertTrue((! solvable) && (degree > 0));
            }
        }
    }
    private PolynomialFunction buildRandomPolynomial(int degree, Random randomizer) {
        final double[] coefficients = new double[degree + 1];
        for (int i = 0; i <= degree; ++i) {
            coefficients[i] = randomizer.nextGaussian();
        }
        return new PolynomialFunction(coefficients);
    }
    @Test
    public void testLargeSample() {
        Random randomizer = new Random(0x5551480dca5b369bl);
        double maxError = 0;
        for (int degree = 0; degree < 10; ++degree) {
            PolynomialFunction p = buildRandomPolynomial(degree, randomizer);

            PolynomialFitter fitter = new PolynomialFitter(new LevenbergMarquardtOptimizer());
            for (int i = 0; i < 40000; ++i) {
                double x = -1.0 + i / 20000.0;
                fitter.addObservedPoint(1.0, x,
                                        p.value(x) + 0.1 * randomizer.nextGaussian());
            }

            final double[] init = new double[degree + 1];
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(init));

            for (double x = -1.0; x < 1.0; x += 0.01) {
                double error = FastMath.abs(p.value(x) - fitted.value(x)) /
                              (1.0 + FastMath.abs(p.value(x)));
                maxError = FastMath.max(maxError, error);
                Assert.assertTrue(FastMath.abs(error) < 0.01);
            }
        }
        Assert.assertTrue(maxError > 0.001);
    }
}
