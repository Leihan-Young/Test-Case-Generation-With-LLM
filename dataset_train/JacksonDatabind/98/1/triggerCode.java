package com.fasterxml.jackson.databind.jsontype.ext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
public class ExternalTypeIdWithEnum1328Test extends BaseMapTest
{
    public void testExample() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(Arrays.asList(new AnimalAndType(AnimalType.Dog, new Dog())));
        List<AnimalAndType> list = mapper.readerFor(new TypeReference<List<AnimalAndType>>() { })
            .readValue(json);
        assertNotNull(list);
    }
}
