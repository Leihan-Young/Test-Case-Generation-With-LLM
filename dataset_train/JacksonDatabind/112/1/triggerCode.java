package com.fasterxml.jackson.databind.deser.creators;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
public class DelegatingArrayCreator2324Test extends BaseMapTest
{
    public void testDeserializeBagOfStrings() throws Exception {
        WithBagOfStrings result = MAPPER.readerFor(WithBagOfStrings.class)
                .readValue("{\"strings\": [ \"a\", \"b\", \"c\"]}");
        assertEquals(3, result.getStrings().size());
    }
}
