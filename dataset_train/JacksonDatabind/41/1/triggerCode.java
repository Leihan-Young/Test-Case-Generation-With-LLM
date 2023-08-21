package com.fasterxml.jackson.databind.interop;
import java.util.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
/**
 * Set of tests to ensure that changes between 2.6 and 2.7 can
 * be handled somewhat gracefully.
 */
public class DeprecatedTypeHandling1102Test extends BaseMapTest
{
    @SuppressWarnings("deprecation")
    public void testDeprecatedTypeResolution() throws Exception
    {
        TypeFactory tf = MAPPER.getTypeFactory();

        // first, with real (if irrelevant) context
        JavaType t = tf.constructType(Point.class, getClass());
        assertEquals(Point.class, t.getRawClass());

        // and then missing context
        JavaType t2 = tf.constructType(Point.class, (Class<?>) null);
        assertEquals(Point.class, t2.getRawClass());

        JavaType ctxt = tf.constructType(getClass());
        JavaType t3 = tf.constructType(Point.class, ctxt);
        assertEquals(Point.class, t3.getRawClass());
    }
}
