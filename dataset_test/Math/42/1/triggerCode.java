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
package org.apache.commons.math.optimization.linear;
import org.junit.Assert;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.Precision;
import org.junit.Test;
public class SimplexSolverTest {
    /**
     * Converts a test string to a {@link LinearConstraint}.
     * Ex: x0 + x1 + x2 + x3 - x12 = 0
     */
    private LinearConstraint equationFromString(int numCoefficients, String s) {
        Relationship relationship;
        if (s.contains(">=")) {
            relationship = Relationship.GEQ;
        } else if (s.contains("<=")) {
            relationship = Relationship.LEQ;
        } else if (s.contains("=")) {
            relationship = Relationship.EQ;
        } else {
            throw new IllegalArgumentException();
        }

        String[] equationParts = s.split("[>|<]?=");
        double rhs = Double.parseDouble(equationParts[1].trim());

        double[] lhs = new double[numCoefficients];
        String left = equationParts[0].replaceAll(" ?x", "");
        String[] coefficients = left.split(" ");
        for (String coefficient : coefficients) {
            double value = coefficient.charAt(0) == '-' ? -1 : 1;
            int index = Integer.parseInt(coefficient.replaceFirst("[+|-]", "").trim());
            lhs[index] = value;
        }
        return new LinearConstraint(lhs, relationship, rhs);
    }
    @Test
    public void testMath713NegativeVariable() {
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] {1.0, 1.0}, 0.0d);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] {1, 0}, Relationship.EQ, 1));

        double epsilon = 1e-6;
        SimplexSolver solver = new SimplexSolver();
        RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);

        Assert.assertTrue(Precision.compareTo(solution.getPoint()[0], 0.0d, epsilon) >= 0);
        Assert.assertTrue(Precision.compareTo(solution.getPoint()[1], 0.0d, epsilon) >= 0);
    }
}
