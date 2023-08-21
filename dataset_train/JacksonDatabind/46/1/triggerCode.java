package com.fasterxml.jackson.databind.type;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JavaType;
/**
 * Simple tests to verify that {@link JavaType} types work to
 * some degree
 */
public class TestJavaType
    extends BaseMapTest
{
    public void testGenericSignature1195() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Method m;
        JavaType t;

        m = Generic1195.class.getMethod("getList");
        t  = tf.constructType(m.getGenericReturnType());
        assertEquals("Ljava/util/List<Ljava/lang/String;>;", t.getGenericSignature());

        m = Generic1195.class.getMethod("getMap");
        t  = tf.constructType(m.getGenericReturnType());
        assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", t.getGenericSignature());

        m = Generic1195.class.getMethod("getGeneric");
        t  = tf.constructType(m.getGenericReturnType());
        assertEquals("Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/String;>;", t.getGenericSignature());
    }
}
