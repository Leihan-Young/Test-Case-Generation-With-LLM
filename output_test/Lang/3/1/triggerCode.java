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
package org.apache.commons.lang3.math;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
/**
 * Unit tests {@link org.apache.commons.lang3.math.NumberUtils}.
 *
 * @version $Id$
 */
public class NumberUtilsTest {
    private boolean checkCreateNumber(final String val) {
        try {
            final Object obj = NumberUtils.createNumber(val);
            if (obj == null) {
                return false;
            }
            return true;
        } catch (final NumberFormatException e) {
            return false;
       }
    }
    /**
     * Test for {(@link NumberUtils#createNumber(String)}
     */
    @Test
    public void testStringCreateNumberEnsureNoPrecisionLoss(){
        String shouldBeFloat = "1.23";
        String shouldBeDouble = "3.40282354e+38";
        String shouldBeBigDecimal = "1.797693134862315759e+308";
        
        assertTrue(NumberUtils.createNumber(shouldBeFloat) instanceof Float);
        assertTrue(NumberUtils.createNumber(shouldBeDouble) instanceof Double);
        assertTrue(NumberUtils.createNumber(shouldBeBigDecimal) instanceof BigDecimal);
    }
}
