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
package org.apache.commons.math3.util;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.dfp.DfpField;
import org.apache.commons.math3.dfp.DfpMath;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
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
    private boolean compareClassMethods(Class<?> class1, Class<?> class2){
        boolean allfound = true;
        for(Method method1 : class1.getDeclaredMethods()){
            if (Modifier.isPublic(method1.getModifiers())){
                Type []params = method1.getGenericParameterTypes();
                try {
                    class2.getDeclaredMethod(method1.getName(), (Class[]) params);
                } catch (NoSuchMethodException e) {
                    allfound = false;
                    System.out.println(class2.getSimpleName()+" does not implement: "+method1);
                }
            }
        }
        return allfound;
    }
    @Test
    public void testMath905LargePositive() {
        final double start = StrictMath.log(Double.MAX_VALUE);
        final double endT = StrictMath.sqrt(2) * StrictMath.sqrt(Double.MAX_VALUE);
        final double end = 2 * StrictMath.log(endT);

        double maxErr = 0;
        for (double x = start; x < end; x += 1e-3) {
            final double tst = FastMath.cosh(x);
            final double ref = Math.cosh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));            
        }
        Assert.assertEquals(0, maxErr, 3);

        for (double x = start; x < end; x += 1e-3) {
            final double tst = FastMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));            
        }
        Assert.assertEquals(0, maxErr, 3);
    }
}
