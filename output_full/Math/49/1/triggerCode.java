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
import java.io.Serializable;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.analysis.function.Abs;
import org.apache.commons.math.analysis.function.Acos;
import org.apache.commons.math.analysis.function.Asin;
import org.apache.commons.math.analysis.function.Atan;
import org.apache.commons.math.analysis.function.Cbrt;
import org.apache.commons.math.analysis.function.Cosh;
import org.apache.commons.math.analysis.function.Cos;
import org.apache.commons.math.analysis.function.Exp;
import org.apache.commons.math.analysis.function.Expm1;
import org.apache.commons.math.analysis.function.Inverse;
import org.apache.commons.math.analysis.function.Log10;
import org.apache.commons.math.analysis.function.Log1p;
import org.apache.commons.math.analysis.function.Log;
import org.apache.commons.math.analysis.function.Sinh;
import org.apache.commons.math.analysis.function.Sin;
import org.apache.commons.math.analysis.function.Sqrt;
import org.apache.commons.math.analysis.function.Tanh;
import org.apache.commons.math.analysis.function.Tan;
import org.apache.commons.math.analysis.function.Floor;
import org.apache.commons.math.analysis.function.Ceil;
import org.apache.commons.math.analysis.function.Rint;
import org.apache.commons.math.analysis.function.Signum;
import org.apache.commons.math.analysis.function.Ulp;
import org.apache.commons.math.analysis.function.Power;
/**
 * Test cases for the {@link OpenMapRealVector} class.
 *
 * @version $Id$
 */
public class SparseRealVectorTest {
        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }
    /* Check that the operations do not throw an exception (cf. MATH-645). */
    @Test
    public void testConcurrentModification() {
        final RealVector u = new OpenMapRealVector(3, 1e-6);
        u.setEntry(0, 1);
        u.setEntry(1, 0);
        u.setEntry(2, 2);

        final RealVector v1 = new OpenMapRealVector(3, 1e-6);
        final double[] v2 = new double[3];
        v1.setEntry(0, 0);
        v2[0] = 0;
        v1.setEntry(1, 3);
        v2[1] = 3;
        v1.setEntry(2, 0);
        v2[2] = 0;

        RealVector w;

        w = u.ebeMultiply(v1);
        w = u.ebeMultiply(v2);

        w = u.ebeDivide(v1);
        w = u.ebeDivide(v2);
    }
}
