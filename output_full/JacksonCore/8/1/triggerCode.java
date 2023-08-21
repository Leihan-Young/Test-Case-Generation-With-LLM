package com.fasterxml.jackson.core.util;
public class TestTextBuffer
    extends com.fasterxml.jackson.core.BaseTest
{
    public void testEmpty() {
        TextBuffer tb = new TextBuffer(new BufferRecycler());
        tb.resetWithEmpty();

        assertTrue(tb.getTextBuffer().length == 0);
        tb.contentsAsString();
        assertTrue(tb.getTextBuffer().length == 0);
    }
}
