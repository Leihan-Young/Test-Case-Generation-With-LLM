package com.fasterxml.jackson.databind.struct;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.*;
/**
 * Unit tests for verifying serialization of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class EnumFormatShapeTest
    extends BaseMapTest
{
    public void testEnumPropertyAsNumber() throws Exception {
        assertEquals(String.format(aposToQuotes("{'color':%s}"), Color.GREEN.ordinal()), MAPPER.writeValueAsString(new ColorWrapper(Color.GREEN)));
    }
}
