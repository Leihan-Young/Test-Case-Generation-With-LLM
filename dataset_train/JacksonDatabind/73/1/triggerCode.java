package com.fasterxml.jackson.databind.deser;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.*;
public class ReadOrWriteOnlyTest extends BaseMapTest
{
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    // [databind#935]
    public void testReadOnlyAndWriteOnly() throws Exception
    {
        String json = MAPPER.writeValueAsString(new ReadXWriteY());
        assertEquals("{\"x\":1}", json);

        ReadXWriteY result = MAPPER.readValue("{\"x\":5, \"y\":6}", ReadXWriteY.class);
        assertNotNull(result);
        assertEquals(1, result.x);
        assertEquals(6, result.y);
    }
}
