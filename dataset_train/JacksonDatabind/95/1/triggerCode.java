package com.fasterxml.jackson.databind.type;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
/**
 * Simple tests to verify that the {@link TypeFactory} constructs
 * type information as expected.
 */
public class TestTypeFactory
    extends BaseMapTest
{    
    /**
     * Test for checking that canonical name handling works ok
     */
    public void testCanonicalNames()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(java.util.Calendar.class);
        String can = t.toCanonical();
        assertEquals("java.util.Calendar", can);
        assertEquals(t, tf.constructFromCanonical(can));

        // Generic maps and collections will default to Object.class if type-erased
        t = tf.constructType(java.util.ArrayList.class);
        can = t.toCanonical();
        assertEquals("java.util.ArrayList<java.lang.Object>", can);
        assertEquals(t, tf.constructFromCanonical(can));

        t = tf.constructType(java.util.TreeMap.class);
        can = t.toCanonical();
        assertEquals("java.util.TreeMap<java.lang.Object,java.lang.Object>", can);
        assertEquals(t, tf.constructFromCanonical(can));

        // And then EnumMap (actual use case for us)
        t = tf.constructMapType(EnumMap.class, EnumForCanonical.class, String.class);
        can = t.toCanonical();
        assertEquals("java.util.EnumMap<com.fasterxml.jackson.databind.type.TestTypeFactory$EnumForCanonical,java.lang.String>", can);
        assertEquals(t, tf.constructFromCanonical(can));

        // [databind#1941]: allow "raw" types too

        t = tf.constructFromCanonical("java.util.List");
        assertEquals(List.class, t.getRawClass());
        assertEquals(CollectionType.class, t.getClass());
        // 01-Mar-2018, tatu: not 100% should we expect type parameters here...
        //    But currently we do NOT get any
        /*
        assertEquals(1, t.containedTypeCount());
        assertEquals(Object.class, t.containedType(0).getRawClass());
        */
        assertEquals(Object.class, t.getContentType().getRawClass());
        can = t.toCanonical();
        assertEquals("java.util.List<java.lang.Object>", can);
        assertEquals(t, tf.constructFromCanonical(can));
    }
}
