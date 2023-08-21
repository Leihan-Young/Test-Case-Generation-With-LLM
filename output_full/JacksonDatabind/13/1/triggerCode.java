package com.fasterxml.jackson.databind.struct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.UnresolvedId;
import com.fasterxml.jackson.databind.struct.TestObjectId.Company;
import com.fasterxml.jackson.databind.struct.TestObjectId.Employee;
/**
 * Unit test to verify handling of Object Id deserialization
 */
public class TestObjectIdDeserialization extends BaseMapTest
{
    private static final String POOL_KEY = "POOL";
    private void assertEmployees(Employee firstEmployee, Employee secondEmployee)
    {
        assertEquals(1, firstEmployee.id);
        assertEquals(2, secondEmployee.id);
        assertEquals(1, firstEmployee.reports.size());
        assertSame(secondEmployee, firstEmployee.reports.get(0)); // Ensure that forward reference was properly resolved and in order.
        assertSame(firstEmployee, secondEmployee.manager); // And that back reference is also properly resolved.
    }
    public void testNullObjectId() throws Exception
    {
        // Ok, so missing Object Id is ok, but so is null.
        
        Identifiable value = MAPPER.readValue
                (aposToQuotes("{'value':3, 'next':null, 'id':null}"), Identifiable.class);
        assertNotNull(value);
        assertEquals(3, value.value);
    }
}
