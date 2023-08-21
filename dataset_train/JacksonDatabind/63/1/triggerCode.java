package com.fasterxml.jackson.databind.deser.exc;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class ExceptionPathTest extends BaseMapTest
{
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testReferenceChainForInnerClass() throws Exception
    {
        String json = MAPPER.writeValueAsString(new Outer());
        try {
            MAPPER.readValue(json, Outer.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            JsonMappingException.Reference reference = e.getPath().get(0);
            assertEquals(getClass().getName()+"$Outer[\"inner\"]", reference.toString());
        }
    }
}
