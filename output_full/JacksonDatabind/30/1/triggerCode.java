package com.fasterxml.jackson.databind.jsontype;
import java.math.BigDecimal;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TestExternalId extends BaseMapTest
{
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes({ @JsonSubTypes.Type(name = "BIG_DECIMAL", value = BigDecimal.class) })
        @JsonSetter(value = "objectValue") 
        private void setValue(Object value) {
            this.value = value;
        }
    public void testBigDecimal965() throws Exception
    {

        Wrapper965 w = new Wrapper965();
        w.typeEnum = Type965.BIG_DECIMAL;
        final String NUM_STR = "-10000000000.0000000001";
        w.value = new BigDecimal(NUM_STR);

        String json = MAPPER.writeValueAsString(w);

        // simple sanity check so serialization is faithful
        if (!json.contains(NUM_STR)) {
            fail("JSON content should contain value '"+NUM_STR+"', does not appear to: "+json);
        }
        
        Wrapper965 w2 = MAPPER.readerFor(Wrapper965.class)
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .readValue(json);

        assertEquals(w.typeEnum, w2.typeEnum);
        assertTrue(String.format("Expected %s = %s; got back %s = %s", w.value.getClass().getSimpleName(), w.value.toString(), w2.value.getClass().getSimpleName(), w2.value.toString()), w.value.equals(w2.value));
    }
}
