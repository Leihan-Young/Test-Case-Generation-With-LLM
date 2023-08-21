package com.fasterxml.jackson.databind.jsontype;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.NoClass;
/**
 * Unit tests related to specialized handling of "default implementation"
 * ({@link JsonTypeInfo#defaultImpl}), as well as related
 * cases that allow non-default settings (such as missing type id).
 */
public class TestPolymorphicWithDefaultImpl extends BaseMapTest
{
    public void testUnknownTypeIDRecovery() throws Exception
    {
        ObjectReader reader = MAPPER.readerFor(CallRecord.class).without(
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        String json = aposToQuotes("{'version':0.0,'application':'123',"
                +"'item':{'type':'xevent','location':'location1'},"
                +"'item2':{'type':'event','location':'location1'}}");
        // can't read item2 - which is valid
        CallRecord r = reader.readValue(json);
        assertNull(r.item);
        assertNotNull(r.item2);

        json = aposToQuotes("{'item':{'type':'xevent','location':'location1'}, 'version':0.0,'application':'123'}");
        CallRecord r3 = reader.readValue(json);
        assertNull(r3.item);
        assertEquals("123", r3.application);
    }
}
