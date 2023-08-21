package com.fasterxml.jackson.databind.type;
import java.lang.reflect.Method;
import java.util.*;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JavaType;
/**
 * Simple tests to verify that {@link JavaType} types work to
 * some degree
 */
public class TestJavaType
    extends BaseMapTest
{
    public void testLocalType728() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Method m = Issue728.class.getMethod("method", CharSequence.class);
        assertNotNull(m);

        // Start with return type
        // first type-erased
        JavaType t = tf.constructType(m.getReturnType());
        assertEquals(CharSequence.class, t.getRawClass());
        // then generic
        t = tf.constructType(m.getGenericReturnType());
        assertEquals(CharSequence.class, t.getRawClass());

        // then parameter type
        t = tf.constructType(m.getParameterTypes()[0]);
        assertEquals(CharSequence.class, t.getRawClass());
        t = tf.constructType(m.getGenericParameterTypes()[0]);
        assertEquals(CharSequence.class, t.getRawClass());
    }
}
