package com.fasterxml.jackson.databind.deser.filter;
import java.io.*;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
public class ProblemHandlerUnknownTypeId2221Test extends BaseMapTest
{
    public void testWithDeserializationProblemHandler() throws Exception {
        final ObjectMapper mapper = new ObjectMapper()
                .enableDefaultTyping();
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public JavaType handleUnknownTypeId(DeserializationContext ctxt, JavaType baseType, String subTypeId, TypeIdResolver idResolver, String failureMsg) throws IOException { //                System.out.println("Print out a warning here"); return ctxt.constructType(Void.class);
            }
        });
        GenericContent processableContent = mapper.readValue(JSON, GenericContent.class);
        assertNotNull(processableContent.getInnerObjects());
        assertEquals(2, processableContent.getInnerObjects().size());
    }
}
