package com.fasterxml.jackson.databind.interop;
import org.springframework.jacksontest.BogusApplicationContext;
import org.springframework.jacksontest.BogusPointcutAdvisor;
import org.springframework.jacksontest.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.mchange.v2.c3p0.jacksontest.ComboPooledDataSource;
import java.util.ArrayList;
import java.util.List;
/**
 * Test case(s) to guard against handling of types that are illegal to handle
 * due to security constraints.
 */
public class IllegalTypesCheckTest extends BaseMapTest
{
    private void _testIllegalType(Class<?> nasty) throws Exception {
        _testIllegalType(nasty.getName());
    }
    private void _testIllegalType(String clsName) throws Exception
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
    public void testC3P0Types() throws Exception
    {
        _testIllegalType(ComboPooledDataSource.class); // [databind#1931]
    }
}
