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
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
public class TestCustomEnumKeyDeserializer extends BaseMapTest
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testCustomEnumValueAndKeyViaModifier() throws IOException
    {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {        
            @Override
            public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
                    final JavaType type, BeanDescription beanDesc,
                    final JsonDeserializer<?> deserializer) {
                return new JsonDeserializer<Enum>() {
                    @Override
                    public Enum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
                        final String str = p.getValueAsString().toLowerCase();
                        return KeyEnum.valueOf(rawClass, str);
                    }
                };
            }

            @Override
            public KeyDeserializer modifyKeyDeserializer(DeserializationConfig config,
                    final JavaType type, KeyDeserializer deserializer)
            {
                if (!type.isEnumType()) {
                    return deserializer;
                }
                return new KeyDeserializer() {
                    @Override
                    public Object deserializeKey(String key, DeserializationContext ctxt)
                            throws IOException
                    {
                        Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();
                        return Enum.valueOf(rawClass, key.toLowerCase());
                    }
                };
            }
        });
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(module);

        // First, enum value as is
        KeyEnum key = mapper.readValue(quote(KeyEnum.replacements.name().toUpperCase()),
                KeyEnum.class);
        assertSame(KeyEnum.replacements, key);

        // and then as key
        EnumMap<KeyEnum,String> map = mapper.readValue(
                aposToQuotes("{'REPlaceMENTS':'foobar'}"),
                new TypeReference<EnumMap<KeyEnum,String>>() { });
        assertEquals(1, map.size());
        assertSame(KeyEnum.replacements, map.keySet().iterator().next());
    }
}
