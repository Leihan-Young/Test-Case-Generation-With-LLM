package com.fasterxml.jackson.databind.deser.creators;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
public class CreatorWithNamingStrategyTest extends BaseMapTest
{
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newObjectMapper()
            .setAnnotationIntrospector(new MyParamIntrospector())
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            ;

    // [databind#2051]
    public void testSnakeCaseWithOneArg() throws Exception
    {
        final String MSG = "1st";
        OneProperty actual = MAPPER.readValue(
                "{\"param_name0\":\""+MSG+"\"}",
                OneProperty.class);
        assertEquals("CTOR:"+MSG, actual.paramName0);
    }
}
