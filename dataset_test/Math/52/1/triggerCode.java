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
package org.apache.commons.math.geometry.euclidean.threed;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;
public class RotationTest {
  private void checkVector(Vector3D v1, Vector3D v2) {
    Assert.assertTrue(v1.subtract(v2).getNorm() < 1.0e-10);
  }
  private void checkAngle(double a1, double a2) {
    Assert.assertEquals(a1, MathUtils.normalizeAngle(a2, a1), 1.0e-10);
  }
  private void checkRotation(Rotation r, double q0, double q1, double q2, double q3) {
    Assert.assertEquals(0, Rotation.distance(r, new Rotation(q0, q1, q2, q3, false)), 1.0e-12);
  }
  @Test
  public void testIssue639(){
      Vector3D u1 = new Vector3D(-1321008684645961.0 /  268435456.0,
                                 -5774608829631843.0 /  268435456.0,
                                 -3822921525525679.0 / 4294967296.0);
      Vector3D u2 =new Vector3D( -5712344449280879.0 /    2097152.0,
                                 -2275058564560979.0 /    1048576.0,
                                  4423475992255071.0 /      65536.0);
      Rotation rot = new Rotation(u1, u2, Vector3D.PLUS_I,Vector3D.PLUS_K);
      Assert.assertEquals( 0.6228370359608200639829222, rot.getQ0(), 1.0e-15);
      Assert.assertEquals( 0.0257707621456498790029987, rot.getQ1(), 1.0e-15);
      Assert.assertEquals(-0.0000000002503012255839931, rot.getQ2(), 1.0e-15);
      Assert.assertEquals(-0.7819270390861109450724902, rot.getQ3(), 1.0e-15);
  }
}
