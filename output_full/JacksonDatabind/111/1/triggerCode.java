package com.fasterxml.jackson.databind.deser.jdk;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.atomic.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
public class JDKAtomicTypesDeserTest
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    public void testNullWithinNested() throws Exception
    {
        final ObjectReader r = MAPPER.readerFor(MyBean2303.class);
        MyBean2303 intRef = r.readValue(" {\"refRef\": 2 } ");
        assertNotNull(intRef.refRef);
        assertNotNull(intRef.refRef.get());
        assertEquals(intRef.refRef.get().get(), new Integer(2));

        MyBean2303 nullRef = r.readValue(" {\"refRef\": null } ");
        assertNotNull(nullRef.refRef);
        assertNotNull(nullRef.refRef.get());
        assertNull(nullRef.refRef.get().get());
    }
}
