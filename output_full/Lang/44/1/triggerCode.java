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
package org.apache.commons.lang;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Unit tests {@link org.apache.commons.lang.NumberUtils}.
 *
 * @author <a href="mailto:rand_mcneely@yahoo.com">Rand McNeely</a>
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Eric Pugh
 * @author Phil Steitz
 * @author Stephen Colebourne
 * @version $Id$
 */
public class NumberUtilsTest extends TestCase {
    private boolean checkCreateNumber(String val) {
        try {
            Object obj = NumberUtils.createNumber(val);
            if (obj == null) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
    public void testLang457() {
        String[] badInputs = new String[] { "l", "L", "f", "F", "junk", "bobL"};
        for(int i=0; i<badInputs.length; i++) {
            try {
                NumberUtils.createNumber(badInputs[i]);
                fail("NumberFormatException was expected for " + badInputs[i]);
            } catch (NumberFormatException e) {
                return; // expected
            }
        }
    }
}
