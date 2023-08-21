package com.fasterxml.jackson.databind.type;
import java.util.*;
import com.fasterxml.jackson.databind.*;
public class RecursiveTypeTest extends BaseMapTest
{
    public void testSuperClassWithReferencedJavaType() {
        TypeFactory tf = objectMapper().getTypeFactory();
        tf.constructType(Base.class); // must be constructed before sub to set the cache correctly
        JavaType subType = tf.constructType(Sub.class);
        // baseTypeFromSub should be a ResolvedRecursiveType in this test
        JavaType baseTypeFromSub = subType.getSuperClass();
        assertNotNull(baseTypeFromSub.getSuperClass());
    }
}
