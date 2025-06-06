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
package org.apache.commons.math.stat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import org.apache.commons.math.TestUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the {@link Frequency} class.
 *
 * @version $Revision$ $Date$
 */
public final class FrequencyTest extends TestCase {
    private long oneL = 1;
    private long twoL = 2;
    private long threeL = 3;
    private int oneI = 1;
    private int twoI = 2;
    private int threeI=3;
    private double tolerance = 10E-15;
    private Frequency f = null;
    public void testAddNonComparable(){
        try {
            f.addValue(new Object()); // This was OK
            fail("Expected ClassCastException");
        } catch (ClassCastException expected) {
        }
        f.clear();
        f.addValue(1);
        try {
            f.addValue(new Object());
            fail("Expected ClassCastException"); // Previously would have been IllegalArgument
        } catch (ClassCastException expected) {
        }
    }
}
