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
package org.apache.commons.math.linear;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.math.BigDecimal;
/**
 * Test cases for the {@link BigMatrixImpl} class.
 *
 * @version $Revision$ $Date$
 */
public final class BigMatrixImplTest extends TestCase {
    /** test issue MATH-209 */
    public void testMath209() {
        BigMatrix a = new BigMatrixImpl(new BigDecimal[][] {
                { new BigDecimal(1), new BigDecimal(2) },
                { new BigDecimal(3), new BigDecimal(4) },
                { new BigDecimal(5), new BigDecimal(6) }
        }, false);
        BigDecimal[] b = a.operate(new BigDecimal[] { new BigDecimal(1), new BigDecimal(1) });
        assertEquals(a.getRowDimension(), b.length);
        assertEquals( 3.0, b[0].doubleValue(), 1.0e-12);
        assertEquals( 7.0, b[1].doubleValue(), 1.0e-12);
        assertEquals(11.0, b[2].doubleValue(), 1.0e-12);
    }
}
