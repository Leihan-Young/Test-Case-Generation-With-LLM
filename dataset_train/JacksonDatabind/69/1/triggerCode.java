package com.fasterxml.jackson.databind.creators;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class Creator1476Test extends BaseMapTest
{
    public void testConstructorChoice() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimplePojo pojo = mapper.readValue("{ \"intField\": 1, \"stringField\": \"foo\" }", SimplePojo.class);

        assertEquals(1, pojo.getIntField());
        assertEquals("foo", pojo.getStringField());
    }
}
