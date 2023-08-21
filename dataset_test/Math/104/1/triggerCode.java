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
package org.apache.commons.math.special;
import org.apache.commons.math.MathException;
import org.apache.commons.math.TestUtils;
import junit.framework.TestCase;
/**
 * @version $Revision$ $Date$
 */
public class GammaTest extends TestCase {
    private void testRegularizedGamma(double expected, double a, double x) {
        try {
            double actualP = Gamma.regularizedGammaP(a, x);
            double actualQ = Gamma.regularizedGammaQ(a, x);
            TestUtils.assertEquals(expected, actualP, 10e-15);
            TestUtils.assertEquals(actualP, 1.0 - actualQ, 10e-15);
        } catch(MathException ex){
            fail(ex.getMessage());
        }
    }
    private void testLogGamma(double expected, double x) {
        double actual = Gamma.logGamma(x);
        TestUtils.assertEquals(expected, actual, 10e-15);
    }
    public void testRegularizedGammaPositivePositive() {
        testRegularizedGamma(0.632120558828558, 1.0, 1.0);
    }
}
