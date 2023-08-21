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
import org.junit.Test;
import org.junit.Assert;
public class RectangularCholeskyDecompositionTest {
    @Test
    public void testMath789() {

        final RealMatrix m1 = MatrixUtils.createRealMatrix(new double[][]{
            {0.013445532, 0.010394690, 0.009881156, 0.010499559},
            {0.010394690, 0.023006616, 0.008196856, 0.010732709},
            {0.009881156, 0.008196856, 0.019023866, 0.009210099},
            {0.010499559, 0.010732709, 0.009210099, 0.019107243}
        });
        RealMatrix root1 = new RectangularCholeskyDecomposition(m1, 1.0e-10).getRootMatrix();
        RealMatrix rebuiltM1 = root1.multiply(root1.transpose());
        Assert.assertEquals(0.0, m1.subtract(rebuiltM1).getNorm(), 1.0e-16);

        final RealMatrix m2 = MatrixUtils.createRealMatrix(new double[][]{
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.0, 0.013445532, 0.010394690, 0.009881156, 0.010499559},
            {0.0, 0.010394690, 0.023006616, 0.008196856, 0.010732709},
            {0.0, 0.009881156, 0.008196856, 0.019023866, 0.009210099},
            {0.0, 0.010499559, 0.010732709, 0.009210099, 0.019107243}
        });
        RealMatrix root2 = new RectangularCholeskyDecomposition(m2, 1.0e-10).getRootMatrix();
        RealMatrix rebuiltM2 = root2.multiply(root2.transpose());
        Assert.assertEquals(0.0, m2.subtract(rebuiltM2).getNorm(), 1.0e-16);

        final RealMatrix m3 = MatrixUtils.createRealMatrix(new double[][]{
            {0.013445532, 0.010394690, 0.0, 0.009881156, 0.010499559},
            {0.010394690, 0.023006616, 0.0, 0.008196856, 0.010732709},
            {0.0, 0.0, 0.0, 0.0, 0.0},
            {0.009881156, 0.008196856, 0.0, 0.019023866, 0.009210099},
            {0.010499559, 0.010732709, 0.0, 0.009210099, 0.019107243}
        });
        RealMatrix root3 = new RectangularCholeskyDecomposition(m3, 1.0e-10).getRootMatrix();
        RealMatrix rebuiltM3 = root3.multiply(root3.transpose());
        Assert.assertEquals(0.0, m3.subtract(rebuiltM3).getNorm(), 1.0e-16);

    }
}
