package com.fasterxml.jackson.databind.misc;
import java.io.IOException;
import java.security.Permission;
import com.fasterxml.jackson.databind.*;
public class AccessFixTest extends BaseMapTest
{
    private void _testCauseOfThrowableIgnoral() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
        IOException e = mapper.readValue("{}", IOException.class);
        assertNotNull(e);
    }
    private void _testCauseOfThrowableIgnoral() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
        IOException e = mapper.readValue("{}", IOException.class);
        assertNotNull(e);
    }
    public void testCauseOfThrowableIgnoral() throws Exception
    {
        final SecurityManager origSecMan = System.getSecurityManager();
        try {
            System.setSecurityManager(new CauseBlockingSecurityManager());
            _testCauseOfThrowableIgnoral();
        } finally {
            System.setSecurityManager(origSecMan);
        }
    }
}
