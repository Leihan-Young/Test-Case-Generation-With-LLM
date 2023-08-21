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
package org.apache.commons.math3.ode.nonstiff;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.TestProblem1;
import org.apache.commons.math3.ode.TestProblem3;
import org.apache.commons.math3.ode.TestProblem4;
import org.apache.commons.math3.ode.TestProblem5;
import org.apache.commons.math3.ode.TestProblemHandler;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
public class DormandPrince853IntegratorTest {
  @Test
  public void testEventsScheduling() {

      FirstOrderDifferentialEquations sincos = new FirstOrderDifferentialEquations() {

          public int getDimension() {
              return 2;
          }

          public void computeDerivatives(double t, double[] y, double[] yDot) {
              yDot[0] =  y[1];
              yDot[1] = -y[0];
          }

      };

      SchedulingChecker sinChecker = new SchedulingChecker(0); // events at 0, PI, 2PI ...
      SchedulingChecker cosChecker = new SchedulingChecker(1); // events at PI/2, 3PI/2, 5PI/2 ...

      FirstOrderIntegrator integ =
              new DormandPrince853Integrator(0.001, 1.0, 1.0e-12, 0.0);
      integ.addEventHandler(sinChecker, 0.01, 1.0e-7, 100);
      integ.addStepHandler(sinChecker);
      integ.addEventHandler(cosChecker, 0.01, 1.0e-7, 100);
      integ.addStepHandler(cosChecker);
      double   t0 = 0.5;
      double[] y0 = new double[] { FastMath.sin(t0), FastMath.cos(t0) };
      double   t  = 10.0;
      double[] y  = new double[2];
      integ.integrate(sincos, t0, y0, t, y);

  }
}
