package com.fasterxml.jackson.core;
import com.fasterxml.jackson.test.BaseTest;
public class TestJsonPointer extends BaseTest
{
    public void testWonkyNumber173() throws Exception
    {
        JsonPointer ptr = JsonPointer.compile("/1e0");
        assertFalse(ptr.matches());
    }
}
