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
package org.apache.commons.math3.complex;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
/**
 * @version $Id$
 */
public class ComplexTest {
    private double inf = Double.POSITIVE_INFINITY;
    private double neginf = Double.NEGATIVE_INFINITY;
    private double nan = Double.NaN;
    private double pi = FastMath.PI;
    private Complex oneInf = new Complex(1, inf);
    private Complex oneNegInf = new Complex(1, neginf);
    private Complex infOne = new Complex(inf, 1);
    private Complex infZero = new Complex(inf, 0);
    private Complex infNaN = new Complex(inf, nan);
    private Complex infNegInf = new Complex(inf, neginf);
    private Complex infInf = new Complex(inf, inf);
    private Complex negInfInf = new Complex(neginf, inf);
    private Complex negInfZero = new Complex(neginf, 0);
    private Complex negInfOne = new Complex(neginf, 1);
    private Complex negInfNaN = new Complex(neginf, nan);
    private Complex negInfNegInf = new Complex(neginf, neginf);
    private Complex oneNaN = new Complex(1, nan);
    private Complex zeroInf = new Complex(0, inf);
    private Complex zeroNaN = new Complex(0, nan);
    private Complex nanInf = new Complex(nan, inf);
    private Complex nanNegInf = new Complex(nan, neginf);
    private Complex nanZero = new Complex(nan, 0);
    @Test
    public void testReciprocalZero() {
        Assert.assertEquals(Complex.ZERO.reciprocal(), Complex.INF);
    }
}
