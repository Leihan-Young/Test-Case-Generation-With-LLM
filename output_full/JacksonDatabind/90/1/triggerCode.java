package com.fasterxml.jackson.databind.creators;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
public class DelegatingArrayCreator1804Test extends BaseMapTest
{
    public void testDelegatingArray1804() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MyType thing = mapper.readValue("[]", MyType.class);
        assertNotNull(thing);
    }
}
