package com.fasterxml.jackson.databind.jsontype;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.NoClass;
/**
 * Unit tests related to specialized handling of "default implementation"
 * ({@link JsonTypeInfo#defaultImpl}), as well as related
 * cases that allow non-default settings (such as missing type id).
 */
public class TestPolymorphicWithDefaultImpl extends BaseMapTest
{
    public void testWithEmptyStringAsNullObject1533() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        AsPropertyWrapper wrapper = mapper.readValue("{ \"value\": \"\" }", AsPropertyWrapper.class);
        assertNull(wrapper.value);
    }
}
