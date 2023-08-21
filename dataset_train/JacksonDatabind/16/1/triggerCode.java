package com.fasterxml.jackson.databind.mixins;
import java.lang.annotation.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class MixinsWithBundlesTest extends BaseMapTest
{
    public void testMixinWithBundles() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper().addMixIn(Foo.class, FooMixin.class);
        String result = mapper.writeValueAsString(new Foo("result"));
        assertEquals("{\"bar\":\"result\"}", result);
    }
}
