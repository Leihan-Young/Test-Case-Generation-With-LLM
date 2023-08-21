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
package org.apache.commons.lang3.builder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
/**
 * Unit tests for {@link org.apache.commons.lang3.builder.ToStringBuilder}.
 *
 * @author Apache Software Foundation
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author <a href="mailto:alex@apache.org">Alex Chaffee</a>
 * @version $Id$
 */
public class ToStringBuilderTest extends TestCase {
    private final Integer base = new Integer(5);
    private final String baseStr = base.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(base));
    /**
     * Create the same toString() as Object.toString().
     * @param o the object to create the string for.
     * @return a String in the Object.toString format.
     */
    private String toBaseString(Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }
    public void testObjectCycle() {
        ObjectCycle a = new ObjectCycle();
        ObjectCycle b = new ObjectCycle();
        a.obj = b;
        b.obj = a;

        String expected = toBaseString(a) + "[" + toBaseString(b) + "[" + toBaseString(a) + "]]";
        assertEquals(expected, a.toString());
        validateNullToStringStyleRegistry();
    }
}
