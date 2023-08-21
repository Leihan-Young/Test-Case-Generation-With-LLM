package com.fasterxml.jackson.databind.interop;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
/**
 * Test case(s) to guard against handling of types that are illegal to handle
 * due to security constraints.
 */
public class IllegalTypesCheckTest extends BaseMapTest
{
    private void _testTypes1737(Class<?> nasty) throws Exception {
        _testTypes1737(nasty.getName());
    }
    private void _testTypes1737(String clsName) throws Exception
    {
        // While usually exploited via default typing let's not require
        // it here; mechanism still the same
        String json = aposToQuotes(
                "{'v':['"+clsName+"','/tmp/foobar.txt']}"
                );
        try {
            MAPPER.readValue(json, PolyWrapper.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            _verifySecurityException(e, clsName);
        }
    }
    public void testJDKTypes1737() throws Exception
    {
        _testTypes1737(java.util.logging.FileHandler.class);
        _testTypes1737(java.rmi.server.UnicastRemoteObject.class);
    }
}
