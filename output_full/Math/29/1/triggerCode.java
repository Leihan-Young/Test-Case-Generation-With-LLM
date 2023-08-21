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
package org.apache.commons.math3.linear;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Asin;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Cbrt;
import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Cosh;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.analysis.function.Expm1;
import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Log10;
import org.apache.commons.math3.analysis.function.Log1p;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Rint;
import org.apache.commons.math3.analysis.function.Signum;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinh;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.analysis.function.Tan;
import org.apache.commons.math3.analysis.function.Tanh;
import org.apache.commons.math3.analysis.function.Ulp;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
public abstract class RealVectorAbstractTest {
    private void doTestAppendVector(final String message, final RealVector v1,
        final RealVector v2, final double delta) {

        final int n1 = v1.getDimension();
        final int n2 = v2.getDimension();
        final RealVector v = v1.append(v2);
        Assert.assertEquals(message, n1 + n2, v.getDimension());
        for (int i = 0; i < n1; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v1.getEntry(i), v.getEntry(i), delta);
        }
        for (int i = 0; i < n2; i++) {
            final String msg = message + ", entry #" + (n1 + i);
            Assert.assertEquals(msg, v2.getEntry(i), v.getEntry(n1 + i), delta);
        }
    }
    private void doTestAppendScalar(final String message, final RealVector v,
        final double d, final double delta) {

        final int n = v.getDimension();
        final RealVector w = v.append(d);
        Assert.assertEquals(message, n + 1, w.getDimension());
        for (int i = 0; i < n; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v.getEntry(i), w.getEntry(i), delta);
        }
        final String msg = message + ", entry #" + n;
        Assert.assertEquals(msg, d, w.getEntry(n), delta);
    }
    private void doTestEbeBinaryOperation(final BinaryOperation op, final boolean mixed) {
        /*
         * Make sure that x, y, z are three different values. Also, x is the
         * preferred value (e.g. the value which is not stored in sparse
         * implementations).
         */
        final double x = getPreferredEntryValue();
        final double y = x + 1d;
        final double z = y + 1d;

        /*
         * This is an attempt at covering most particular cases of combining
         * two values.
         *
         * 1. Addition
         *    --------
         * The following cases should be covered
         * (2 * x) + (-x)
         * (-x) + 2 * x
         * x + y
         * y + x
         * y + z
         * y + (x - y)
         * (y - x) + x
         *
         * The values to be considered are: x, y, z, 2 * x, -x, x - y, y - x.
         *
         * 2. Subtraction
         *    -----------
         * The following cases should be covered
         * (2 * x) - x
         * x - y
         * y - x
         * y - z
         * y - (y - x)
         * (y + x) - y
         *
         * The values to be considered are: x, y, z, x + y, y - x.
         *
         * 3. Multiplication
         *    --------------
         * (x * x) * (1 / x)
         * (1 / x) * (x * x)
         * x * y
         * y * x
         * y * z
         *
         * The values to be considered are: x, y, z, 1 / x, x * x.
         *
         * 4. Division
         *    --------
         * (x * x) / x
         * x / y
         * y / x
         * y / z
         *
         * The values to be considered are: x, y, z, x * x.
         *
         * Also to be considered NaN, POSITIVE_INFINITY, NEGATIVE_INFINITY.
         */
        final double[] values = {x, y, z, 2 * x, -x, 1 / x, x * x, x + y, x - y, y - x};
        final double[] data1 = new double[values.length * values.length];
        final double[] data2 = new double[values.length * values.length];
        int k = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                data1[k] = values[i];
                data2[k] = values[j];
                ++k;
            }
        }
        final RealVector v1 = create(data1);
        final RealVector v2 = mixed ? createAlien(data2) : create(data2);
        final RealVector actual;
        switch (op) {
            case ADD:
                actual = v1.add(v2);
                break;
            case SUB:
                actual = v1.subtract(v2);
                break;
            case MUL:
                actual = v1.ebeMultiply(v2);
                break;
            case DIV:
                actual = v1.ebeDivide(v2);
                break;
            default:
                throw new AssertionError("unexpected value");
        }
        final double[] expected = new double[data1.length];
        for (int i = 0; i < expected.length; i++) {
            switch (op) {
                case ADD:
                    expected[i] = data1[i] + data2[i];
                    break;
                case SUB:
                    expected[i] = data1[i] - data2[i];
                    break;
                case MUL:
                    expected[i] = data1[i] * data2[i];
                    break;
                case DIV:
                    expected[i] = data1[i] / data2[i];
                    break;
                default:
                    throw new AssertionError("unexpected value");
            }
        }
        for (int i = 0; i < expected.length; i++) {
            final String msg = "entry #"+i+", left = "+data1[i]+", right = " + data2[i];
            Assert.assertEquals(msg, expected[i], actual.getEntry(i), 0.0);
        }
    }
    private void doTestEbeBinaryOperationDimensionMismatch(final BinaryOperation op) {
        final int n = 10;
        switch (op) {
            case ADD:
                create(new double[n]).add(create(new double[n + 1]));
                break;
            case SUB:
                create(new double[n]).subtract(create(new double[n + 1]));
                break;
            case MUL:
                create(new double[n]).ebeMultiply(create(new double[n + 1]));
                break;
            case DIV:
                create(new double[n]).ebeDivide(create(new double[n + 1]));
                break;
            default:
                throw new AssertionError("unexpected value");
        }
    }
    @Test
    public void testEbeDivideMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, true);
    }
}
