package com.fasterxml.jackson.core.json;
import com.fasterxml.jackson.core.*;
public class LocationInObjectTest extends BaseTest
{
    public void testOffsetWithObjectFieldsUsingReader() throws Exception
    {
        final JsonFactory f = new JsonFactory();
        char[] c = "{\"f1\":\"v1\",\"f2\":{\"f3\":\"v3\"},\"f4\":[true,false],\"f5\":5}".toCharArray();
        //            1      6      11    16 17    22      28    33 34 39      46    51
        JsonParser p = f.createParser(c);

        assertEquals(JsonToken.START_OBJECT, p.nextToken());

        assertEquals(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals(1L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.VALUE_STRING, p.nextToken());
        assertEquals(6L, p.getTokenLocation().getCharOffset());

        assertEquals("f2", p.nextFieldName());
        assertEquals(11L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.START_OBJECT, p.nextValue());
        assertEquals(16L, p.getTokenLocation().getCharOffset());

        assertEquals("f3", p.nextFieldName());
        assertEquals(17L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.VALUE_STRING, p.nextValue());
        assertEquals(22L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.END_OBJECT, p.nextToken());

        assertEquals("f4", p.nextFieldName());
        assertEquals(28L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.START_ARRAY, p.nextValue());
        assertEquals(33L, p.getTokenLocation().getCharOffset());

        assertEquals(JsonToken.VALUE_TRUE, p.nextValue());
        assertEquals(34L, p.getTokenLocation().getCharOffset());

        assertEquals(JsonToken.VALUE_FALSE, p.nextValue());
        assertEquals(39L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.END_ARRAY, p.nextToken());

        assertEquals("f5", p.nextFieldName());
        assertEquals(46L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(51L, p.getTokenLocation().getCharOffset());
        assertEquals(JsonToken.END_OBJECT, p.nextToken());

        p.close();
    }
}
