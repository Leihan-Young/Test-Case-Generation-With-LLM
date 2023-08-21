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
package org.apache.commons.math.analysis;
import org.apache.commons.math.MathException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Testcase for UnivariateRealSolver.
 * Because Brent-Dekker is guaranteed to converge in less than the default
 * maximum iteration count due to bisection fallback, it is quite hard to
 * debug. I include measured iteration counts plus one in order to detect
 * regressions. On average Brent-Dekker should use 4..5 iterations for the
 * default absolute accuracy of 10E-8 for sinus and the quintic function around
 * zero, and 5..10 iterations for the other zeros.
 * 
 * @version $Revision:670469 $ $Date:2008-06-23 10:01:38 +0200 (lun., 23 juin 2008) $ 
 */
public final class BrentSolverTest extends TestCase {
    public void testRootEndpoints() throws Exception {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = new BrentSolver(f);
        
        // endpoint is root
        double result = solver.solve(Math.PI, 4);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());

        result = solver.solve(3, Math.PI);
        assertEquals(result, Math.PI, solver.getAbsoluteAccuracy());
    }
}
