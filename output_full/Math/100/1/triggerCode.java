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
package org.apache.commons.math.estimation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.math.estimation.EstimatedParameter;
import org.apache.commons.math.estimation.EstimationException;
import org.apache.commons.math.estimation.EstimationProblem;
import org.apache.commons.math.estimation.GaussNewtonEstimator;
import org.apache.commons.math.estimation.WeightedMeasurement;
import junit.framework.*;
/**
 * <p>Some of the unit tests are re-implementations of the MINPACK <a
 * href="http://www.netlib.org/minpack/ex/file17">file17</a> and <a
 * href="http://www.netlib.org/minpack/ex/file22">file22</a> test files. 
 * The redistribution policy for MINPACK is available <a
 * href="http://www.netlib.org/minpack/disclaimer">here</a>, for
 * convenience, it is reproduced below.</p>

 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>
 *    Minpack Copyright Notice (1999) University of Chicago.
 *    All rights reserved
 * </td></tr>
 * <tr><td>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <ol>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 * <li>Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.</li>
 * <li>The end-user documentation included with the redistribution, if any,
 *     must include the following acknowledgment:
 *     <code>This product includes software developed by the University of
 *           Chicago, as Operator of Argonne National Laboratory.</code>
 *     Alternately, this acknowledgment may appear in the software itself,
 *     if and wherever such third-party acknowledgments normally appear.</li>
 * <li><strong>WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
 *     WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
 *     UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
 *     THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
 *     OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
 *     OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
 *     OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
 *     USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
 *     THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
 *     DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
 *     UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
 *     BE CORRECTED.</strong></li>
 * <li><strong>LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
 *     HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
 *     ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
 *     INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
 *     ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
 *     PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
 *     SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
 *     (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
 *     EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
 *     POSSIBILITY OF SUCH LOSS OR DAMAGES.</strong></li>
 * <ol></td></tr>
 * </table>

 * @author Argonne National Laboratory. MINPACK project. March 1980 (original fortran minpack tests)
 * @author Burton S. Garbow (original fortran minpack tests)
 * @author Kenneth E. Hillstrom (original fortran minpack tests)
 * @author Jorge J. More (original fortran minpack tests)
 * @author Luc Maisonobe (non-minpack tests and minpack tests Java translation)
 */
public class GaussNewtonEstimatorTest
  extends TestCase {
  public void testBoundParameters() throws EstimationException {
      EstimatedParameter[] p = {
        new EstimatedParameter("unbound0", 2, false),
        new EstimatedParameter("unbound1", 2, false),
        new EstimatedParameter("bound",    2, true)
      };
      LinearProblem problem = new LinearProblem(new LinearMeasurement[] {
        new LinearMeasurement(new double[] { 1.0, 1.0, 1.0 },
                              new EstimatedParameter[] { p[0], p[1], p[2] },
                              3.0),
        new LinearMeasurement(new double[] { 1.0, -1.0, 1.0 },
                              new EstimatedParameter[] { p[0], p[1], p[2] },
                              1.0),
        new LinearMeasurement(new double[] { 1.0, 3.0, 2.0 },
                              new EstimatedParameter[] { p[0], p[1], p[2] },
                              7.0)
      });

      GaussNewtonEstimator estimator = new GaussNewtonEstimator(100, 1.0e-6, 1.0e-6);
      estimator.estimate(problem);
      assertTrue(estimator.getRMS(problem) < 1.0e-10);
      double[][] covariances = estimator.getCovariances(problem);
      int i0 = 0, i1 = 1;
      if (problem.getUnboundParameters()[0].getName().endsWith("1")) {
          i0 = 1;
          i1 = 0;
      }
      assertEquals(11.0 / 24, covariances[i0][i0], 1.0e-10);
      assertEquals(-3.0 / 24, covariances[i0][i1], 1.0e-10);
      assertEquals(-3.0 / 24, covariances[i1][i0], 1.0e-10);
      assertEquals( 3.0 / 24, covariances[i1][i1], 1.0e-10);

      double[] errors = estimator.guessParametersErrors(problem);
      assertEquals(0, errors[i0], 1.0e-10);
      assertEquals(0, errors[i1], 1.0e-10);

  }
}
