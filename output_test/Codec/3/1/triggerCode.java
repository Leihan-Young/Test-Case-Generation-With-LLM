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
package org.apache.commons.codec.language;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Tests {@link DoubleMetaphone}.
 * <p>
 * The test data was extracted from Stephen Woodbridge's
 * <a href="http://swoodbridge.com/DoubleMetaPhone/surnames.txt">php test program</a>.
 * 
 * @see http://swoodbridge.com/DoubleMetaPhone/surnames.txt
 * @version $Id$
 */
public class DoubleMetaphone2Test extends TestCase {
    /**
     * Test alternative encoding.
     */
    public void testDoubleMetaphoneAlternate() {
        String value = null;
        for (int i = 0; i < TEST_DATA.length; i++) {
            value = TEST_DATA[i][0];
            assertEquals("Test [" + i + "]=" + value, TEST_DATA[i][2], doubleMetaphone.doubleMetaphone(value, true));
        }
    }
}
