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
package org.apache.commons.math3.analysis.differentiation;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test for class {@link DerivativeStructure}.
 */
public class DerivativeStructureTest {
    private void checkF0F1(DerivativeStructure ds, double value, double...derivatives) {

        // check dimension
        Assert.assertEquals(derivatives.length, ds.getFreeParameters());

        // check value, directly and also as 0th order derivative
        Assert.assertEquals(value, ds.getValue(), 1.0e-15);
        Assert.assertEquals(value, ds.getPartialDerivative(new int[ds.getFreeParameters()]), 1.0e-15);

        // check first order derivatives
        for (int i = 0; i < derivatives.length; ++i) {
            int[] orders = new int[derivatives.length];
            orders[i] = 1;
            Assert.assertEquals(derivatives[i], ds.getPartialDerivative(orders), 1.0e-15);
        }

    }
    private void checkEquals(DerivativeStructure ds1, DerivativeStructure ds2, double epsilon) {

        // check dimension
        Assert.assertEquals(ds1.getFreeParameters(), ds2.getFreeParameters());
        Assert.assertEquals(ds1.getOrder(), ds2.getOrder());

        int[] derivatives = new int[ds1.getFreeParameters()];
        int sum = 0;
        while (true) {

            if (sum <= ds1.getOrder()) {
                Assert.assertEquals(ds1.getPartialDerivative(derivatives),
                                    ds2.getPartialDerivative(derivatives),
                                    epsilon);
            }

            boolean increment = true;
            sum = 0;
            for (int i = derivatives.length - 1; i >= 0; --i) {
                if (increment) {
                    if (derivatives[i] == ds1.getOrder()) {
                        derivatives[i] = 0;
                    } else {
                        derivatives[i]++;
                        increment = false;
                    }
                }
                sum += derivatives[i];
            }
            if (increment) {
                return;
            }

        }

    }
    @Test
    public void testAtan2SpecialCases() {

        DerivativeStructure pp =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, +0.0),
                                          new DerivativeStructure(2, 2, 1, +0.0));
        Assert.assertEquals(0, pp.getValue(), 1.0e-15);
        Assert.assertEquals(+1, FastMath.copySign(1, pp.getValue()), 1.0e-15);

        DerivativeStructure pn =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, +0.0),
                                          new DerivativeStructure(2, 2, 1, -0.0));
        Assert.assertEquals(FastMath.PI, pn.getValue(), 1.0e-15);

        DerivativeStructure np =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, -0.0),
                                          new DerivativeStructure(2, 2, 1, +0.0));
        Assert.assertEquals(0, np.getValue(), 1.0e-15);
        Assert.assertEquals(-1, FastMath.copySign(1, np.getValue()), 1.0e-15);

        DerivativeStructure nn =
                DerivativeStructure.atan2(new DerivativeStructure(2, 2, 1, -0.0),
                                          new DerivativeStructure(2, 2, 1, -0.0));
        Assert.assertEquals(-FastMath.PI, nn.getValue(), 1.0e-15);

    }
}
