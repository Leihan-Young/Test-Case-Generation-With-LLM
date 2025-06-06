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
package org.apache.commons.math.dfp;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
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
    public void testIssue567() {
        DfpField field = new DfpField(100);
        Assert.assertEquals(0.0, field.getZero().toDouble(), MathUtils.SAFE_MIN);
        Assert.assertEquals(0.0, field.newDfp(0.0).toDouble(), MathUtils.SAFE_MIN);
        Assert.assertEquals(-1, FastMath.copySign(1, field.newDfp(-0.0).toDouble()), MathUtils.EPSILON);
        Assert.assertEquals(+1, FastMath.copySign(1, field.newDfp(+0.0).toDouble()), MathUtils.EPSILON);
    }
}
