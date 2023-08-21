package com.fasterxml.jackson.databind.deser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.atomic.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
public class TestJDKAtomicTypes
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    public void testEmpty1256() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        String json = mapper.writeValueAsString(new Issue1256Bean());
        assertEquals("{}", json);
    }
}
