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
package org.apache.commons.math.util;
import org.apache.commons.math.dfp.Dfp;
import org.apache.commons.math.dfp.DfpField;
import org.apache.commons.math.dfp.DfpMath;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
public class FastMathTest {
    private static final double MAX_ERROR_ULP = 0.51;
    private static final int NUMBER_OF_TRIALS = 1000;
    private DfpField field;
    private RandomGenerator generator;
    private Dfp cosh(Dfp x) {
      return DfpMath.exp(x).add(DfpMath.exp(x.negate())).divide(2);
    }
    private Dfp sinh(Dfp x) {
      return DfpMath.exp(x).subtract(DfpMath.exp(x.negate())).divide(2);
    }
    private Dfp tanh(Dfp x) {
      return sinh(x).divide(cosh(x));
    }
    private Dfp cbrt(Dfp x) {
      boolean negative=false;

      if (x.lessThan(field.getZero())) {
          negative = true;
          x = x.negate();
      }

      Dfp y = DfpMath.pow(x, field.getOne().divide(3));

      if (negative) {
          y = y.negate();
      }

      return y;
    }
    @Test
    public void testMinMaxFloat() {
        float[][] pairs = {
            { -50.0f, 50.0f },
            {  Float.POSITIVE_INFINITY, 1.0f },
            {  Float.NEGATIVE_INFINITY, 1.0f },
            {  Float.NaN, 1.0f },
            {  Float.POSITIVE_INFINITY, 0.0f },
            {  Float.NEGATIVE_INFINITY, 0.0f },
            {  Float.NaN, 0.0f },
            {  Float.NaN, Float.NEGATIVE_INFINITY },
            {  Float.NaN, Float.POSITIVE_INFINITY }
        };
        for (float[] pair : pairs) {
            Assert.assertEquals("min(" + pair[0] + ", " + pair[1] + ")", Math.min(pair[0], pair[1]), FastMath.min(pair[0], pair[1]), MathUtils.EPSILON);
            Assert.assertEquals("min(" + pair[1] + ", " + pair[0] + ")", Math.min(pair[1], pair[0]), FastMath.min(pair[1], pair[0]), MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[0] + ", " + pair[1] + ")", Math.max(pair[0], pair[1]), FastMath.max(pair[0], pair[1]), MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[1] + ", " + pair[0] + ")", Math.max(pair[1], pair[0]), FastMath.max(pair[1], pair[0]), MathUtils.EPSILON);
        }
    }
}
