/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.regression;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision$ $Date$
 */
public final class SimpleRegressionTest extends TestCase {
    public void testSSENonNegative() {
        double[] y = { 8915.102, 8919.302, 8923.502 };
        double[] x = { 1.107178495E2, 1.107264895E2, 1.107351295E2 };
        SimpleRegression reg = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            reg.addData(x[i], y[i]);
        }
        assertTrue(reg.getSumSquaredErrors() >= 0.0);
    } 
}
