package com.fasterxml.jackson.databind.ser.jdk;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class SqlDateSerializationTest extends BaseMapTest
{
    public void testSqlDateConfigOverride() throws Exception
    {
        ObjectMapper mapper = newObjectMapper();
        mapper.configOverride(java.sql.Date.class)
            .setFormat(JsonFormat.Value.forPattern("yyyy+MM+dd"));        
        assertEquals("\"1980+04+14\"", mapper.writeValueAsString(java.sql.Date.valueOf("1980-04-14")));
    }
}
