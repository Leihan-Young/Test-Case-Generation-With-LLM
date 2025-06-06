package com.fasterxml.jackson.databind.objectid;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class AlwaysAsReferenceFirstTest extends BaseMapTest
{
    public void testIssue1255() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Foo mo = new Foo();
        mo.bar1 = new Bar();
        mo.bar2 = mo.bar1;

        String json = mapper.writeValueAsString(mo);

        Foo result = mapper.readValue(json, Foo.class);
        assertNotNull(result);
    }
}
