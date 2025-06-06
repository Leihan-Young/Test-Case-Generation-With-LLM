/*
 * This file was automatically generated by EvoSuite
 * Thu Aug 03 02:29:16 GMT 2023
 */

package org.apache.commons.math3.distribution;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.RealMatrix;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true) 
public class MultivariateNormalDistribution_ESTest extends MultivariateNormalDistribution_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      double[][] doubleArray1 = new double[2][1];
      MultivariateNormalDistribution multivariateNormalDistribution0 = null;
      try {
        multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // 1 != 2
         //
         verifyException("org.apache.commons.math3.distribution.MultivariateNormalDistribution", e);
      }
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      doubleArray0[0] = 3.6938826366068014E-196;
      doubleArray0[1] = 3.6938826366068014E-196;
      double[][] doubleArray1 = new double[2][1];
      doubleArray1[0] = doubleArray0;
      doubleArray1[1] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
      double double0 = multivariateNormalDistribution0.density(doubleArray0);
      assertEquals(Double.NaN, double0, 0.01);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      doubleArray0[0] = 3.6938826366068014E-196;
      doubleArray0[1] = 3.6938826366068014E-196;
      double[][] doubleArray1 = new double[2][1];
      doubleArray1[0] = doubleArray0;
      doubleArray1[1] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
      RealMatrix realMatrix0 = multivariateNormalDistribution0.getCovariances();
      assertEquals(2, realMatrix0.getColumnDimension());
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      double[][] doubleArray0 = new double[8][1];
      MultivariateNormalDistribution multivariateNormalDistribution0 = null;
      try {
        multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0[0], doubleArray0);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // 8 != 1
         //
         verifyException("org.apache.commons.math3.distribution.MultivariateNormalDistribution", e);
      }
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      double[] doubleArray0 = new double[1];
      doubleArray0[0] = (-4058.7790478160164);
      double[][] doubleArray1 = new double[1][8];
      doubleArray1[0] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = null;
      try {
        multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // -4,058.779 is smaller than, or equal to, the minimum (0): not positive definite matrix: value -4,058.779 at index 0
         //
         verifyException("org.apache.commons.math3.distribution.MultivariateNormalDistribution", e);
      }
  }

  @Test(timeout = 4000)
  public void test5()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      doubleArray0[0] = 3.6938826366068014E-196;
      doubleArray0[1] = 3.6938826366068014E-196;
      double[][] doubleArray1 = new double[2][1];
      doubleArray1[0] = doubleArray0;
      doubleArray1[1] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
      double[] doubleArray2 = new double[6];
      try { 
        multivariateNormalDistribution0.density(doubleArray2);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // 6 != 2
         //
         verifyException("org.apache.commons.math3.distribution.MultivariateNormalDistribution", e);
      }
  }

  @Test(timeout = 4000)
  public void test6()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      doubleArray0[0] = 3.6938826366068014E-196;
      doubleArray0[1] = 3.6938826366068014E-196;
      double[][] doubleArray1 = new double[2][1];
      doubleArray1[0] = doubleArray0;
      doubleArray1[1] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
      double[] doubleArray2 = multivariateNormalDistribution0.getStandardDeviations();
      assertArrayEquals(new double[] {1.9219476154689548E-98, 1.9219476154689548E-98}, doubleArray2, 0.01);
  }

  @Test(timeout = 4000)
  public void test7()  throws Throwable  {
      double[] doubleArray0 = new double[2];
      doubleArray0[0] = 3.6938826366068014E-196;
      doubleArray0[1] = 3.6938826366068014E-196;
      double[][] doubleArray1 = new double[2][1];
      doubleArray1[0] = doubleArray0;
      doubleArray1[1] = doubleArray0;
      MultivariateNormalDistribution multivariateNormalDistribution0 = new MultivariateNormalDistribution(doubleArray0, doubleArray1);
      double[][] doubleArray2 = multivariateNormalDistribution0.sample(291);
      assertNotSame(doubleArray1, doubleArray2);
  }
}
