package com.fasterxml.jackson.databind.jsontype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
public class GenericTypeId1735Test extends BaseMapTest
{
    public void testNestedTypeCheck1735() throws Exception
    {
        try {
            MAPPER.readValue(aposToQuotes(
"{'w':{'type':'java.util.HashMap<java.lang.String,java.lang.String>'}}"),
                    Wrapper1735.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "not subtype of");
        }
    }
}
