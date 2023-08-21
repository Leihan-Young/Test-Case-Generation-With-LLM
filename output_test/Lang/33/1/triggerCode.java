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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
/**
 * Unit tests {@link org.apache.commons.lang3.ClassUtils}.
 *
 * @author Apache Software Foundation
 * @author Gary D. Gregory
 * @author Tomasz Blachowicz
 * @version $Id$
 */
public class ClassUtilsTest extends TestCase {
    private void assertGetClassReturnsClass( Class<?> c ) throws Exception {
        assertEquals( c, ClassUtils.getClass( c.getName() ) );
    }
    private void assertGetClassThrowsException( String className, Class<?> exceptionType ) throws Exception {
        try {
            ClassUtils.getClass( className );
            fail( "ClassUtils.getClass() should fail with an exception of type " + exceptionType.getName() + " when given class name \"" + className + "\"." );
        }
        catch( Exception e ) {
            assertTrue( exceptionType.isAssignableFrom( e.getClass() ) );
        }
    }
    private void assertGetClassThrowsNullPointerException( String className ) throws Exception {
        assertGetClassThrowsException( className, NullPointerException.class );
    }
    private void assertGetClassThrowsClassNotFound( String className ) throws Exception {
        assertGetClassThrowsException( className, ClassNotFoundException.class );
    }
    public void testToClass_object() {
        assertNull(ClassUtils.toClass(null));

        assertSame(ArrayUtils.EMPTY_CLASS_ARRAY, ClassUtils.toClass(ArrayUtils.EMPTY_OBJECT_ARRAY));

        assertTrue(Arrays.equals(new Class[] { String.class, Integer.class, Double.class }, ClassUtils.toClass(new Object[] { "Test", 1, 99d })));

        assertTrue(Arrays.equals(new Class[] { String.class, null, Double.class }, ClassUtils.toClass(new Object[] { "Test", null, 99d })));
    }
}
