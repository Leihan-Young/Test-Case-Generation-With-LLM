package com.fasterxml.jackson.core.base64;
import java.io.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.testsupport.ThrottledInputStream;
public class Base64GenerationTest
    extends com.fasterxml.jackson.core.BaseTest
{
    private final static String WIKIPEDIA_BASE64_TEXT = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
    private final static byte[] WIKIPEDIA_BASE64_AS_BYTES;
    /**********************************************************
     */
    
    private void _testSimpleBinaryWrite(boolean useCharBased) throws Exception
    {
        /* Let's only test the standard base64 variant; but write
         * values in root, array and object contexts.
         */
        Base64Variant b64v = Base64Variants.getDefaultVariant();
        JsonFactory jf = new JsonFactory();

        for (int i = 0; i < 3; ++i) {
            JsonGenerator gen;
            ByteArrayOutputStream bout = new ByteArrayOutputStream(200);
            if (useCharBased) {
                gen = jf.createGenerator(new OutputStreamWriter(bout, "UTF-8"));
            } else {
                gen = jf.createGenerator(bout, JsonEncoding.UTF8);
            }

            switch (i) {
            case 0: // root
                gen.writeBinary(b64v, WIKIPEDIA_BASE64_AS_BYTES, 0, WIKIPEDIA_BASE64_AS_BYTES.length);
                break;
            case 1: // array
                gen.writeStartArray();
                gen.writeBinary(b64v, WIKIPEDIA_BASE64_AS_BYTES, 0, WIKIPEDIA_BASE64_AS_BYTES.length);
                gen.writeEndArray();
                break;
            default: // object
                gen.writeStartObject();
                gen.writeFieldName("field");
                gen.writeBinary(b64v, WIKIPEDIA_BASE64_AS_BYTES, 0, WIKIPEDIA_BASE64_AS_BYTES.length);
                gen.writeEndObject();
                break;
            }
            gen.close();

            JsonParser jp = jf.createParser(new ByteArrayInputStream(bout.toByteArray()));
            
            // Need to skip other events before binary data:
            switch (i) {
            case 0:
                break;
            case 1:
                assertEquals(JsonToken.START_ARRAY, jp.nextToken());
                break;
            default:
                assertEquals(JsonToken.START_OBJECT, jp.nextToken());
                assertEquals(JsonToken.FIELD_NAME, jp.nextToken());
                break;
            }
            assertEquals(JsonToken.VALUE_STRING, jp.nextToken());
            String actualValue = jp.getText();
            jp.close();
            assertEquals(WIKIPEDIA_BASE64_ENCODED, actualValue);
        }
    }
    @SuppressWarnings("resource")
    private void _testStreamingWrites(JsonFactory jf, boolean useBytes) throws Exception
    {
        final byte[] INPUT = TEXT4.getBytes("UTF-8");
        for (Base64Variant variant : VARIANTS) {
            final String EXP_OUTPUT = "[" + quote(variant.encode(INPUT))+"]";
            for (boolean passLength : new boolean[] { true, false }) {
                for (int chunkSize : new int[] { 1, 2, 3, 4, 7, 11, 29, 5000 }) {
//System.err.println(""+variant+", length "+passLength+", chunk "+chunkSize);
                    
                    JsonGenerator jgen;
                    
                    final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    if (useBytes) {
                        jgen = jf.createGenerator(bytes);
                    } else {
                        jgen = jf.createGenerator(new OutputStreamWriter(bytes, "UTF-8"));
                    }
                    jgen.writeStartArray();
                    int length = passLength ? INPUT.length : -1;
                    InputStream data = new ThrottledInputStream(INPUT, chunkSize);
                    jgen.writeBinary(variant, data, length);
                    jgen.writeEndArray();
                    jgen.close();
                    String JSON = bytes.toString("UTF-8");
                    assertEquals(EXP_OUTPUT, JSON);
                }
            }
        }
    }
    public void testBinaryAsEmbeddedObject() throws Exception
    {
        JsonGenerator g;

        StringWriter sw = new StringWriter();
        g = JSON_F.createGenerator(sw);
        g.writeEmbeddedObject(WIKIPEDIA_BASE64_AS_BYTES);
        g.close();
        assertEquals(quote(WIKIPEDIA_BASE64_ENCODED), sw.toString());

        ByteArrayOutputStream bytes =  new ByteArrayOutputStream(100);
        g = JSON_F.createGenerator(bytes);
        g.writeEmbeddedObject(WIKIPEDIA_BASE64_AS_BYTES);
        g.close();
        assertEquals(quote(WIKIPEDIA_BASE64_ENCODED), bytes.toString("UTF-8"));
    }
}
