package com.fasterxml.jackson.databind.objectid;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class AlwaysAsReferenceFirstTest extends BaseMapTest
{
    public void testIssue1607() throws Exception
    {
        String json = MAPPER.writeValueAsString(new ReallyAlwaysContainer());
        assertEquals(aposToQuotes("{'alwaysClass':1,'alwaysProp':2}"), json);
    }
}
