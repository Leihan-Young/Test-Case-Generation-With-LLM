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
package org.apache.commons.math.geometry;
import org.apache.commons.math.geometry.Vector3D;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.exception.MathArithmeticException;
import org.junit.Assert;
import org.junit.Test;
public class Vector3DTest {
    private void checkVector(Vector3D v, double x, double y, double z) {
        Assert.assertEquals(x, v.getX(), 1.0e-12);
        Assert.assertEquals(y, v.getY(), 1.0e-12);
        Assert.assertEquals(z, v.getZ(), 1.0e-12);
    }
    @Test
    public void testCrossProductCancellation() {
        Vector3D v1 = new Vector3D(9070467121.0, 4535233560.0, 1);
        Vector3D v2 = new Vector3D(9070467123.0, 4535233561.0, 1);
        checkVector(Vector3D.crossProduct(v1, v2), -1, 2, 1);

        double scale    = FastMath.scalb(1.0, 100);
        Vector3D big1   = new Vector3D(scale, v1);
        Vector3D small2 = new Vector3D(1 / scale, v2);
        checkVector(Vector3D.crossProduct(big1, small2), -1, 2, 1);

    }
}
