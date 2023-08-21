package com.fasterxml.jackson.databind.seq;
import com.fasterxml.jackson.databind.*;
/**
 * Tests to verify aspects of error recover for reading using
 * iterator.
 */
public class ReadRecoveryTest extends BaseMapTest
{
    public void testSimpleRootRecovery() throws Exception
    {
        final String JSON = aposToQuotes("{'a':3}{'a':27,'foo':[1,2],'b':{'x':3}}  {'a':1,'b':2} ");

        MappingIterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(JSON);
        Bean bean = it.nextValue();

        assertNotNull(bean);
        assertEquals(3, bean.a);

        // second one problematic
        try {
            it.nextValue();
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"foo\"");
        }

        // but should recover nicely
        bean = it.nextValue();
        assertNotNull(bean);
        assertEquals(1, bean.a);
        assertEquals(2, bean.b);

        assertFalse(it.hasNextValue());
        
        it.close();
    }
}
