package com.fasterxml.jackson.databind.jsontype.ext;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
public class ExternalTypeId999Test extends BaseMapTest
{
    public void testExternalTypeId() throws Exception
    {
        TypeReference<?> type = new TypeReference<Message<FooPayload>>() { };

        Message<?> msg = MAPPER.readValue(aposToQuotes("{ 'type':'foo', 'payload': {} }"), type);
        assertNotNull(msg);
        assertNotNull(msg.payload);
        assertEquals("foo", msg.type);

        // and then with different order
        msg = MAPPER.readValue(aposToQuotes("{'payload': {}, 'type':'foo' }"), type);
        assertNotNull(msg);
        assertNotNull(msg.payload);
        assertEquals("foo", msg.type);
    }
}
