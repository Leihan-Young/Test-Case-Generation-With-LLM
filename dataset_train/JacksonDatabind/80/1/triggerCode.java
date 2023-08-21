package com.fasterxml.jackson.databind.jsontype;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
/**
 * Separate tests for verifying that "type name" type id mechanism
 * works.
 */
public class TestTypeNames extends BaseMapTest
{
    /**********************************************************
     */

    private final ObjectMapper MAPPER = objectMapper();

    public void testBaseTypeId1616() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Collection<NamedType> subtypes = new StdSubtypeResolver().collectAndResolveSubtypesByTypeId(
                mapper.getDeserializationConfig(),
                // note: `null` is fine here as `AnnotatedMember`:
                null,
                mapper.constructType(Base1616.class));
        assertEquals(2, subtypes.size());
        Set<String> ok = new HashSet<>(Arrays.asList("A", "B"));
        for (NamedType type : subtypes) {
            String id = type.getName();
            if (!ok.contains(id)) {
                fail("Unexpected id '"+id+"' (mapping to: "+type.getType()+"), should be one of: "+ok);
            }
        }
    }
}
