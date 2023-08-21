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
package org.apache.commons.lang3;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Random;
/**
 * Unit tests {@link org.apache.commons.lang3.RandomStringUtils}.
 *
 * @version $Id$
 */
public class RandomStringUtilsTest extends junit.framework.TestCase {
    /**
     * Computes Chi-Square statistic given observed and expected counts
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     */
    private double chiSquare(int[] expected, int[] observed) {
        double sumSq = 0.0d;
        double dev = 0.0d;
        for (int i = 0; i < observed.length; i++) {
            dev = observed[i] - expected[i];
            sumSq += dev * dev / expected[i];
        }
        return sumSq;
    }           
    public void testLANG807() {
        try {
            RandomStringUtils.random(3,5,5,false,false);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) { // distinguish from Random#nextInt message
            final String msg = ex.getMessage();
            assertTrue("Message (" + msg + ") must contain 'start'", msg.contains("start"));
            assertTrue("Message (" + msg + ") must contain 'end'", msg.contains("end"));
        }
    }
}
