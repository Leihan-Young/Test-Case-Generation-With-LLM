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
import java.util.Arrays;
import junit.framework.TestCase;
/**
 * Tests ArrayUtils add methods.
 *
 * @author Gary D. Gregory
 * @version $Id$
 */
public class ArrayUtilsAddTest extends TestCase {
    public void testJira567(){
        Number[] n;
        // Valid array construction
        n = ArrayUtils.addAll(new Number[]{Integer.valueOf(1)}, new Long[]{Long.valueOf(2)});
        assertEquals(2,n.length);
        assertEquals(Number.class,n.getClass().getComponentType());
        try {
            // Invalid - can't store Long in Integer array
               n = ArrayUtils.addAll(new Integer[]{Integer.valueOf(1)}, new Long[]{Long.valueOf(2)});
               fail("Should have generated IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }
}
