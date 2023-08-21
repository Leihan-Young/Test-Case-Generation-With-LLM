package com.fasterxml.jackson.databind.type;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.type.TypeFactory;
public class TestTypeFactoryWithRecursiveTypes extends BaseMapTest {
    public void testBasePropertiesIncludedWhenSerializingSubWhenSubTypeLoadedAfterBaseType() throws IOException {
        TypeFactory tf = TypeFactory.defaultInstance();
        tf.constructType(Base.class);
        tf.constructType(Sub.class);
        Sub sub = new Sub();
        String serialized = objectMapper().writeValueAsString(sub);
        assertEquals("{\"base\":1,\"sub\":2}", serialized);
    }
}
