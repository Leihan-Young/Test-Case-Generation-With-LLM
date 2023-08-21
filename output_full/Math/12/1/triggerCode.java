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
package org.apache.commons.math3.distribution;
import java.util.ArrayList;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Abstract base class for {@link RealDistribution} tests.
 * <p>
 * To create a concrete test class for a continuous distribution
 * implementation, first implement makeDistribution() to return a distribution
 * instance to use in tests. Then implement each of the test data generation
 * methods below.  In each case, the test points and test values arrays
 * returned represent parallel arrays of inputs and expected values for the
 * distribution returned by makeDistribution().  Default implementations
 * are provided for the makeInverseXxx methods that just invert the mapping
 * defined by the arrays returned by the makeCumulativeXxx methods.
 * <p>
 * makeCumulativeTestPoints() -- arguments used to test cumulative probabilities
 * makeCumulativeTestValues() -- expected cumulative probabilites
 * makeDensityTestValues() -- expected density values at cumulativeTestPoints
 * makeInverseCumulativeTestPoints() -- arguments used to test inverse cdf
 * makeInverseCumulativeTestValues() -- expected inverse cdf values
 * <p>
 * To implement additional test cases with different distribution instances and
 * test data, use the setXxx methods for the instance data in test cases and
 * call the verifyXxx methods to verify results.
 * <p>
 * Error tolerance can be overriden by implementing getTolerance().
 * <p>
 * Test data should be validated against reference tables or other packages
 * where possible, and the source of the reference data and/or validation
 * should be documented in the test cases.  A framework for validating
 * distribution data against R is included in the /src/test/R source tree.
 * <p>
 * See {@link NormalDistributionTest} and {@link ChiSquaredDistributionTest}
 * for examples.
 *
 * @version $Id$
 */
public abstract class RealDistributionAbstractTest {
    private RealDistribution distribution;
    private double tolerance = 1E-4;
    private double[] cumulativeTestPoints;
    private double[] cumulativeTestValues;
    private double[] inverseCumulativeTestPoints;
    private double[] inverseCumulativeTestValues;
    private double[] densityTestValues;
    public abstract RealDistribution makeDistribution();
    public abstract double[] makeCumulativeTestPoints();
    public abstract double[] makeCumulativeTestValues();
    public abstract double[] makeDensityTestValues();
    /**
     * Serialization and deserialization loop of the {@link #distribution}.
     */
    private RealDistribution deepClone()
        throws IOException,
               ClassNotFoundException {
        // Serialize to byte array.
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        final ObjectOutputStream oOut = new ObjectOutputStream(bOut);
        oOut.writeObject(distribution);
        final byte[] data = bOut.toByteArray();

        // Deserialize from byte array.
        final ByteArrayInputStream bIn = new ByteArrayInputStream(data);
        final ObjectInputStream oIn = new ObjectInputStream(bIn);
        final Object clone = oIn.readObject();
        oIn.close();

        return (RealDistribution) clone;
    }
    @Test
    public void testDistributionClone()
        throws IOException,
               ClassNotFoundException {
        // Construct a distribution and initialize its internal random
        // generator, using a fixed seed for deterministic results.
        distribution.reseedRandomGenerator(123);
        distribution.sample();

        // Clone the distribution.
        final RealDistribution cloned = deepClone();

        // Make sure they still produce the same samples.
        final double s1 = distribution.sample();
        final double s2 = cloned.sample();
        Assert.assertEquals(s1, s2, 0d);
    }
}
