package com.fasterxml.jackson.databind.util;
import java.io.*;
import java.util.UUID;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
public class TestTokenBuffer extends BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();
    private void _verifyOutputContext(JsonGenerator gen1, JsonGenerator gen2)
    {
        _verifyOutputContext(gen1.getOutputContext(), gen2.getOutputContext());
    }
    private void _verifyOutputContext(JsonStreamContext ctxt1, JsonStreamContext ctxt2)
    {
        if (ctxt1 == null) {
            if (ctxt2 == null) {
                return;
            }
            fail("Context 1 null, context 2 not null: "+ctxt2);
        } else if (ctxt2 == null) {
            fail("Context 2 null, context 1 not null: "+ctxt1);
        }
        if (!ctxt1.getTypeDesc().equals(ctxt2.getTypeDesc())) {
            fail("Different output context: token-buffer's = "+ctxt1+", json-generator's: "+ctxt2);
        }

        if (ctxt1.inObject()) {
            assertTrue(ctxt2.inObject());
            String str1 = ctxt1.getCurrentName();
            String str2 = ctxt2.getCurrentName();

            if ((str1 != str2) && !str1.equals(str2)) {
                fail("Expected name '"+str2+"' (JsonParser), TokenBuffer had '"+str1+"'");
            }
        } else if (ctxt1.inArray()) {
            assertTrue(ctxt2.inArray());
            assertEquals(ctxt1.getCurrentIndex(), ctxt2.getCurrentIndex());
        }
        _verifyOutputContext(ctxt1.getParent(), ctxt2.getParent());
    }
    /**********************************************************
     */

    // for [databind#984]: ensure output context handling identical
    public void testOutputContext() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec
        StringWriter w = new StringWriter();
        JsonGenerator gen = MAPPER.getFactory().createGenerator(w);
 
        // test content: [{"a":1,"b":{"c":2}},{"a":2,"b":{"c":3}}]

        buf.writeStartArray();
        gen.writeStartArray();
        _verifyOutputContext(buf, gen);
        
        buf.writeStartObject();
        gen.writeStartObject();
        _verifyOutputContext(buf, gen);
        
        buf.writeFieldName("a");
        gen.writeFieldName("a");
        _verifyOutputContext(buf, gen);

        buf.writeNumber(1);
        gen.writeNumber(1);
        _verifyOutputContext(buf, gen);

        buf.writeFieldName("b");
        gen.writeFieldName("b");
        _verifyOutputContext(buf, gen);

        buf.writeStartObject();
        gen.writeStartObject();
        _verifyOutputContext(buf, gen);
        
        buf.writeFieldName("c");
        gen.writeFieldName("c");
        _verifyOutputContext(buf, gen);

        buf.writeNumber(2);
        gen.writeNumber(2);
        _verifyOutputContext(buf, gen);

        buf.writeEndObject();
        gen.writeEndObject();
        _verifyOutputContext(buf, gen);

        buf.writeEndObject();
        gen.writeEndObject();
        _verifyOutputContext(buf, gen);

        buf.writeEndArray();
        gen.writeEndArray();
        _verifyOutputContext(buf, gen);
        
        buf.close();
        gen.close();
    }
}
