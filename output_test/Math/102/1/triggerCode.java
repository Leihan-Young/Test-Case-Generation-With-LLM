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
package org.apache.commons.math.stat.inference;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the ChiSquareTestImpl class.
 *
 * @version $Revision$ $Date$
 */
public class ChiSquareTestTest extends TestCase {
    public void testChiSquareLargeTestStatistic() throws Exception {
        double[] exp = new double[] {
            3389119.5, 649136.6, 285745.4, 25357364.76, 11291189.78, 543628.0, 
            232921.0, 437665.75
        };

        long[] obs = new long[] {
            2372383, 584222, 257170, 17750155, 7903832, 489265, 209628, 393899
        };
        org.apache.commons.math.stat.inference.ChiSquareTestImpl csti =
            new org.apache.commons.math.stat.inference.ChiSquareTestImpl(); 
        double cst = csti.chiSquareTest(exp, obs); 
        assertEquals("chi-square p-value", 0.0, cst, 1E-3);
        assertEquals( "chi-square test statistic",  114875.90421929007, testStatistic.chiSquare(exp, obs), 1E-9);
    }
}
