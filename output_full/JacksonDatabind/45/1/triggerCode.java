package com.fasterxml.jackson.databind.ser;
import java.io.*;
import java.text.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;
public class DateSerializationTest
    extends BaseMapTest
{
    /**
     * Test to ensure that the default shape is correctly inferred as string or numeric,
     * when this shape is not explicitly set with a <code>@JsonFormat</code> annotation
     */
    public void testDateDefaultShape() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        // No @JsonFormat => default to user config
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(new DateAsDefaultBean(0L));
        assertEquals(aposToQuotes("{'date':0}"), json);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBean(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000+0000'}"), json);

        // Empty @JsonFormat => default to user config
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithEmptyJsonFormat(0L));
        assertEquals(aposToQuotes("{'date':0}"), json);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithEmptyJsonFormat(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000+0000'}"), json);

        // @JsonFormat with Shape.ANY and pattern => STRING shape, regardless of user config
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithPattern(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01'}"), json);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithPattern(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01'}"), json);

        // @JsonFormat with Shape.ANY and locale => STRING shape, regardless of user config
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithLocale(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000+0000'}"), json);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithLocale(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000+0000'}"), json);

        // @JsonFormat with Shape.ANY and timezone => STRING shape, regardless of user config
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithTimezone(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T01:00:00.000+0100'}"), json);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        json = mapper.writeValueAsString(new DateAsDefaultBeanWithTimezone(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01T01:00:00.000+0100'}"), json);
    }
}
