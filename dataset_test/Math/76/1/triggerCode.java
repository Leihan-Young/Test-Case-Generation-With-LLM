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
package org.apache.commons.math.linear;
import org.junit.Assert;
import org.junit.Test;
public class SingularValueSolverTest {
    private double residual(RealMatrix a, double[] x, double[] b) {
        double[] ax = a.operate(x);
        double sum = 0;
        for (int i = 0; i < ax.length; ++i) {
            sum += (ax[i] - b[i]) * (ax[i] - b[i]);
        }
        return Math.sqrt(sum);
    }
    @Test
    public void testMath320A() {
        RealMatrix rm = new Array2DRowRealMatrix(new double[][] {
            { 1.0, 2.0, 3.0 }, { 2.0, 3.0, 4.0 }, { 3.0, 5.0, 7.0 }
        });
        double s439  = Math.sqrt(439.0);
        double[] reference = new double[] {
            Math.sqrt(3.0 * (21.0 + s439)), Math.sqrt(3.0 * (21.0 - s439))
        };
        SingularValueDecomposition svd =
            new SingularValueDecompositionImpl(rm);

        // check we get the expected theoretical singular values
        double[] singularValues = svd.getSingularValues();
        Assert.assertEquals(reference.length, singularValues.length);
        for (int i = 0; i < reference.length; ++i) {
            Assert.assertEquals(reference[i], singularValues[i], 4.0e-13);
        }

        // check the decomposition allows to recover the original matrix
        RealMatrix recomposed = svd.getU().multiply(svd.getS()).multiply(svd.getVT());
        Assert.assertEquals(0.0, recomposed.subtract(rm).getNorm(), 5.0e-13);

        // check we can solve a singular system
        double[] b = new double[] { 5.0, 6.0, 7.0 };
        double[] resSVD = svd.getSolver().solve(b);
        Assert.assertEquals(rm.getColumnDimension(), resSVD.length);

        // check the solution really minimizes the residuals
        double svdMinResidual = residual(rm, resSVD, b);
        double epsilon = 2 * Math.ulp(svdMinResidual);
        double h = 0.1;
        int    k = 3;
        for (double d0 = -k * h; d0 <= k * h; d0 += h) {
            for (double d1 = -k * h ; d1 <= k * h; d1 += h) {
                for (double d2 = -k * h; d2 <= k * h; d2 += h) {
                    double[] x = new double[] { resSVD[0] + d0, resSVD[1] + d1, resSVD[2] + d2 };
                    Assert.assertTrue((residual(rm, x, b) - svdMinResidual) > -epsilon);
                }
            }
        }

    }
}
