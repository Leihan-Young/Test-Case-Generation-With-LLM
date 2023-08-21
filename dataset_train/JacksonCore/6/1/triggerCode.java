package com.fasterxml.jackson.core;
import com.fasterxml.jackson.test.BaseTest;
public class TestJsonPointer extends BaseTest
{
    public void testIZeroIndex() throws Exception
    {
        JsonPointer ptr = JsonPointer.compile("/0");
        assertEquals(0, ptr.getMatchingIndex());
        ptr = JsonPointer.compile("/00");
        assertEquals(-1, ptr.getMatchingIndex());
    }
}
