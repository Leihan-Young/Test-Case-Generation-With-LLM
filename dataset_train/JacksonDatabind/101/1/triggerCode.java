package com.fasterxml.jackson.databind.struct;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
/**
 * Unit tests for verifying that basic {@link JsonUnwrapped} annotation
 * handling works as expected; some more advanced tests are separated out
 * to more specific test classes (like prefix/suffix handling).
 */
public class TestUnwrapped extends BaseMapTest
{
    public void testIssue2088UnwrappedFieldsAfterLastCreatorProp() throws Exception
    {
        Issue2088Bean bean = MAPPER.readValue("{\"x\":1,\"a\":2,\"y\":3,\"b\":4}", Issue2088Bean.class);
        assertEquals(1, bean.x);
        assertEquals(2, bean.w.a);
        assertEquals(3, bean.y);
        assertEquals(4, bean.w.b);
    }
}
