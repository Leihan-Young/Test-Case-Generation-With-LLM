package com.fasterxml.jackson.databind.creators;
import java.util.Collections;
import java.util.Set;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class ArrayDelegatorCreatorForCollectionTest extends BaseMapTest
{
    public void testUnmodifiable() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Class<?> unmodSetType = Collections.unmodifiableSet(Collections.<String>emptySet()).getClass();
        mapper.addMixIn(unmodSetType, UnmodifiableSetMixin.class);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        final String EXPECTED_JSON = "[\""+unmodSetType.getName()+"\",[]]";

        Set<?> foo = mapper.readValue(EXPECTED_JSON, Set.class);
        assertTrue(foo.isEmpty());
    }
}
