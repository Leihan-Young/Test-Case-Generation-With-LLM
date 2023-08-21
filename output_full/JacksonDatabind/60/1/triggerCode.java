package com.fasterxml.jackson.databind.jsontype;
import org.junit.Assert;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TestDefaultWithCreators
    extends BaseMapTest
{
    public void testWithCreatorAndJsonValue() throws Exception
    {
        final byte[] BYTES = new byte[] { 1, 2, 3, 4, 5 };
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        String json = mapper.writeValueAsString(new Bean1385Wrapper(
                new Bean1385(BYTES)
        ));
        Bean1385Wrapper result = mapper.readValue(json, Bean1385Wrapper.class);
        assertNotNull(result);
        assertNotNull(result.value);
        assertEquals(Bean1385.class, result.value.getClass());
        Bean1385 b = (Bean1385) result.value;
        Assert.assertArrayEquals(BYTES, b.raw);
    }
}
