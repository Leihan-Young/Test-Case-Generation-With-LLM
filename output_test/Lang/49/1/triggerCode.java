/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.lang.math;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the {@link Fraction} class
 *
 * @author Stephen Colebourne
 * @author C. Scott Ananian
 * @version $Id$
 */
public class FractionTest extends TestCase {
    private static final int SKIP = 500;  //53
    public void testReduce() {
        Fraction f = null;
        
        f = Fraction.getFraction(50, 75);
        Fraction result = f.reduce();
        assertEquals(2, result.getNumerator());
        assertEquals(3, result.getDenominator());

        f = Fraction.getFraction(-2, -3);
        result = f.reduce();
        assertEquals(2, result.getNumerator());
        assertEquals(3, result.getDenominator());

        f = Fraction.getFraction(2, -3);
        result = f.reduce();
        assertEquals(-2, result.getNumerator());
        assertEquals(3, result.getDenominator());

        f = Fraction.getFraction(-2, 3);
        result = f.reduce();
        assertEquals(-2, result.getNumerator());
        assertEquals(3, result.getDenominator());
        assertSame(f, result);

        f = Fraction.getFraction(2, 3);
        result = f.reduce();
        assertEquals(2, result.getNumerator());
        assertEquals(3, result.getDenominator());
        assertSame(f, result);

        f = Fraction.getFraction(0, 1);
        result = f.reduce();
        assertEquals(0, result.getNumerator());
        assertEquals(1, result.getDenominator());
        assertSame(f, result);

        f = Fraction.getFraction(0, 100);
        result = f.reduce();
        assertEquals(0, result.getNumerator());
        assertEquals(1, result.getDenominator());
        assertSame(result, Fraction.ZERO);
    }
}
