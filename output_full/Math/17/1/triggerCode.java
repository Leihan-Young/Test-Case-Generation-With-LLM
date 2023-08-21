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
package org.apache.commons.math3.dfp;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class DfpTest {
    private DfpField field;
    private Dfp pinf;
    private Dfp ninf;
    private Dfp nan;
    private Dfp snan;
    private Dfp qnan;
    private void test(Dfp x, Dfp y, int flags, String desc)
    {
        boolean b = x.equals(y);

        if (!x.equals(y) && !x.unequal(y))  // NaNs involved
            b = (x.toString().equals(y.toString()));

        if (x.equals(field.newDfp("0")))  // distinguish +/- zero
            b = (b && (x.toString().equals(y.toString())));

        b = (b && x.getField().getIEEEFlags() == flags);

        if (!b)
            Assert.assertTrue("assersion failed "+desc+" x = "+x.toString()+" flags = "+x.getField().getIEEEFlags(), b);

        x.getField().clearIEEEFlags();
    }
    private void cmptst(Dfp a, Dfp b, String op, boolean result, double num)
    {
        if (op == "equal")
            if (a.equals(b) != result)
                Assert.fail("assersion failed.  "+op+" compare #"+num);

        if (op == "unequal")
            if (a.unequal(b) != result)
                Assert.fail("assersion failed.  "+op+" compare #"+num);

        if (op == "lessThan")
            if (a.lessThan(b) != result)
                Assert.fail("assersion failed.  "+op+" compare #"+num);

        if (op == "greaterThan")
            if (a.greaterThan(b) != result)
                Assert.fail("assersion failed.  "+op+" compare #"+num);
    }
    @Test
    public void testMultiply()
    {
        test(field.newDfp("1").multiply(field.newDfp("1")),      // Basic tests   1*1 = 1
             field.newDfp("1"),
             0, "Multiply #1");

        test(field.newDfp("1").multiply(1),             // Basic tests   1*1 = 1
             field.newDfp("1"),
             0, "Multiply #2");

        test(field.newDfp("-1").multiply(field.newDfp("1")),     // Basic tests   -1*1 = -1
             field.newDfp("-1"),
             0, "Multiply #3");

        test(field.newDfp("-1").multiply(1),            // Basic tests   -1*1 = -1
             field.newDfp("-1"),
             0, "Multiply #4");

        // basic tests with integers
        test(field.newDfp("2").multiply(field.newDfp("3")),
             field.newDfp("6"),
             0, "Multiply #5");

        test(field.newDfp("2").multiply(3),
             field.newDfp("6"),
             0, "Multiply #6");

        test(field.newDfp("-2").multiply(field.newDfp("3")),
             field.newDfp("-6"),
             0, "Multiply #7");

        test(field.newDfp("-2").multiply(3),
             field.newDfp("-6"),
             0, "Multiply #8");

        test(field.newDfp("2").multiply(field.newDfp("-3")),
             field.newDfp("-6"),
             0, "Multiply #9");

        test(field.newDfp("-2").multiply(field.newDfp("-3")),
             field.newDfp("6"),
             0, "Multiply #10");

        //multiply by zero

        test(field.newDfp("-2").multiply(field.newDfp("0")),
             field.newDfp("-0"),
             0, "Multiply #11");

        test(field.newDfp("-2").multiply(0),
             field.newDfp("-0"),
             0, "Multiply #12");

        test(field.newDfp("2").multiply(field.newDfp("0")),
             field.newDfp("0"),
             0, "Multiply #13");

        test(field.newDfp("2").multiply(0),
             field.newDfp("0"),
             0, "Multiply #14");

        test(field.newDfp("2").multiply(pinf),
             pinf,
             0, "Multiply #15");

        test(field.newDfp("2").multiply(ninf),
             ninf,
             0, "Multiply #16");

        test(field.newDfp("-2").multiply(pinf),
             ninf,
             0, "Multiply #17");

        test(field.newDfp("-2").multiply(ninf),
             pinf,
             0, "Multiply #18");

        test(ninf.multiply(field.newDfp("-2")),
             pinf,
             0, "Multiply #18.1");

        test(field.newDfp("5e131071").multiply(2),
             pinf,
             DfpField.FLAG_OVERFLOW, "Multiply #19");

        test(field.newDfp("5e131071").multiply(field.newDfp("1.999999999999999")),
             field.newDfp("9.9999999999999950000e131071"),
             0, "Multiply #20");

        test(field.newDfp("-5e131071").multiply(2),
             ninf,
             DfpField.FLAG_OVERFLOW, "Multiply #22");

        test(field.newDfp("-5e131071").multiply(field.newDfp("1.999999999999999")),
             field.newDfp("-9.9999999999999950000e131071"),
             0, "Multiply #23");

        test(field.newDfp("1e-65539").multiply(field.newDfp("1e-65539")),
             field.newDfp("1e-131078"),
             DfpField.FLAG_UNDERFLOW, "Multiply #24");

        test(field.newDfp("1").multiply(nan),
             nan,
             0, "Multiply #25");

        test(nan.multiply(field.newDfp("1")),
             nan,
             0, "Multiply #26");

        test(nan.multiply(pinf),
             nan,
             0, "Multiply #27");

        test(pinf.multiply(nan),
             nan,
             0, "Multiply #27");

        test(pinf.multiply(field.newDfp("0")),
             nan,
             DfpField.FLAG_INVALID, "Multiply #28");

        test(field.newDfp("0").multiply(pinf),
             nan,
             DfpField.FLAG_INVALID, "Multiply #29");

        test(pinf.multiply(pinf),
             pinf,
             0, "Multiply #30");

        test(ninf.multiply(pinf),
             ninf,
             0, "Multiply #31");

        test(pinf.multiply(ninf),
             ninf,
             0, "Multiply #32");

        test(ninf.multiply(ninf),
             pinf,
             0, "Multiply #33");

        test(pinf.multiply(1),
             pinf,
             0, "Multiply #34");

        test(pinf.multiply(0),
             nan,
             DfpField.FLAG_INVALID, "Multiply #35");

        test(nan.multiply(1),
             nan,
             0, "Multiply #36");

        test(field.newDfp("1").multiply(10000),
             field.newDfp("10000"),
             0, "Multiply #37");

        test(field.newDfp("2").multiply(1000000),
             field.newDfp("2000000"),
             0, "Multiply #38");

        test(field.newDfp("1").multiply(-1),
             field.newDfp("-1"),
             0, "Multiply #39");
    }
}
