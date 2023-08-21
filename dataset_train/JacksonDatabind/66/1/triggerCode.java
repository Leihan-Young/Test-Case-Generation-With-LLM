package com.fasterxml.jackson.databind.module;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.junit.Test;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
public class TestCustomEnumKeyDeserializer extends BaseMapTest
{
    public void testCustomEnumKeySerializerWithPolymorphic() throws IOException
    {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(SuperTypeEnum.class, new JsonDeserializer<SuperTypeEnum>() {
            @Override
            public SuperTypeEnum deserialize(JsonParser p, DeserializationContext deserializationContext)
                    throws IOException
            {
                return SuperTypeEnum.valueOf(p.getText());
            }
        });
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(simpleModule);

        SuperType superType = mapper.readValue("{\"someMap\": {\"FOO\": \"bar\"}}",
                SuperType.class);
        assertEquals("Deserialized someMap.FOO should equal bar", "bar", superType.someMap.get(SuperTypeEnum.FOO));
    }
}
