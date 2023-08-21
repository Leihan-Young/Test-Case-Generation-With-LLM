package com.fasterxml.jackson.databind.jsontype;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TestExternalId extends BaseMapTest
{
    public void testExternalTypeIdWithNull() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerSubtypes(ValueBean.class);
        ExternalBean b;
        b = mapper.readValue(aposToQuotes("{'bean':null,'extType':'vbean'}"),
                ExternalBean.class);
        assertNotNull(b);
        b = mapper.readValue(aposToQuotes("{'extType':'vbean','bean':null}"),
                ExternalBean.class);
        assertNotNull(b);
    }
}
