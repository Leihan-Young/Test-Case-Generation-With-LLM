package com.fasterxml.jackson.databind.ser;
import java.io.*;
import java.text.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;
public class DateSerializationTest
    extends BaseMapTest
{
    public void testFormatWithoutPattern() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss"));
        String json = mapper.writeValueAsString(new DateAsDefaultBeanWithTimezone(0L));
        assertEquals(aposToQuotes("{'date':'1970-01-01X01:00:00'}"), json);
    }
}
