package com.fasterxml.jackson.core.json;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import java.io.*;
import java.net.URL;
import java.util.*;
/**
 * Set of basic unit tests for verifying that the basic parser
 * functionality works as expected.
 */
public class TestJsonParser
    extends com.fasterxml.jackson.core.BaseTest
{
    private final JsonFactory JSON_FACTORY = new JsonFactory();
    private void _testIntern(boolean useStream, boolean enableIntern, String expName) throws IOException
    {
        JsonFactory f = new JsonFactory();
        f.configure(JsonFactory.Feature.INTERN_FIELD_NAMES, enableIntern);
        assertEquals(enableIntern, f.isEnabled(JsonFactory.Feature.INTERN_FIELD_NAMES));
        final String JSON = "{ \""+expName+"\" : 1}";
        JsonParser jp = useStream ?
            createParserUsingStream(f, JSON, "UTF-8") : createParserUsingReader(f, JSON);
            
        assertToken(JsonToken.START_OBJECT, jp.nextToken());
        assertToken(JsonToken.FIELD_NAME, jp.nextToken());
        // needs to be same of cours
        String actName = jp.getCurrentName();
        assertEquals(expName, actName);
        if (enableIntern) {
            assertSame(expName, actName);
        } else {
            assertNotSame(expName, actName);
        }
        jp.close();
    }
    private void _testNameEscaping(boolean useStream) throws IOException
    {
        final Map<String,String> NAME_MAP = new LinkedHashMap<String,String>();
        NAME_MAP.put("", "");
        NAME_MAP.put("\\\"funny\\\"", "\"funny\"");
        NAME_MAP.put("\\\\", "\\");
        NAME_MAP.put("\\r", "\r");
        NAME_MAP.put("\\n", "\n");
        NAME_MAP.put("\\t", "\t");
        NAME_MAP.put("\\r\\n", "\r\n");
        NAME_MAP.put("\\\"\\\"", "\"\"");
        NAME_MAP.put("Line\\nfeed", "Line\nfeed");
        NAME_MAP.put("Yet even longer \\\"name\\\"!", "Yet even longer \"name\"!");

        int entry = 0;
        for (Map.Entry<String,String> en : NAME_MAP.entrySet()) {
            ++entry;
            String input = en.getKey();
            String expResult = en.getValue();
            final String DOC = "{ \""+input+"\":null}";
            JsonParser jp = useStream ?
                    JSON_FACTORY.createParser(new ByteArrayInputStream(DOC.getBytes("UTF-8")))
                : JSON_FACTORY.createParser(new StringReader(DOC));

            assertToken(JsonToken.START_OBJECT, jp.nextToken());
            assertToken(JsonToken.FIELD_NAME, jp.nextToken());
            // first, sanity check (field name == getText()
            String act = jp.getCurrentName();
            assertEquals(act, getAndVerifyText(jp));
            if (!expResult.equals(act)) {
                String msg = "Failed for name #"+entry+"/"+NAME_MAP.size();
                if (expResult.length() != act.length()) {
                    fail(msg+": exp length "+expResult.length()+", actual "+act.length());
                }
                assertEquals(msg, expResult, act);
            }
            assertToken(JsonToken.VALUE_NULL, jp.nextToken());
            assertToken(JsonToken.END_OBJECT, jp.nextToken());
            jp.close();
        }
    }
    @SuppressWarnings("resource")
    private void _testLongText(int LEN) throws Exception
    {
        StringBuilder sb = new StringBuilder(LEN + 100);
        Random r = new Random(99);
        while (sb.length() < LEN) {
            sb.append(r.nextInt());
            sb.append(" xyz foo");
            if (r.nextBoolean()) {
                sb.append(" and \"bar\"");
            } else if (r.nextBoolean()) {
                sb.append(" [whatever].... ");
            } else {
                // Let's try some more 'exotic' chars
                sb.append(" UTF-8-fu: try this {\u00E2/\u0BF8/\uA123!} (look funny?)");
            }
            if (r.nextBoolean()) {
                if (r.nextBoolean()) {
                    sb.append('\n');
                } else if (r.nextBoolean()) {
                    sb.append('\r');
                } else {
                    sb.append("\r\n");
                }
            }
        }
        final String VALUE = sb.toString();
        
        // Let's use real generator to get JSON done right
        StringWriter sw = new StringWriter(LEN + (LEN >> 2));
        JsonGenerator jg = JSON_FACTORY.createGenerator(sw);
        jg.writeStartObject();
        jg.writeFieldName("doc");
        jg.writeString(VALUE);
        jg.writeEndObject();
        jg.close();
        
        final String DOC = sw.toString();

        for (int type = 0; type < 3; ++type) {
            JsonParser jp;

            switch (type) {
            default:
                jp = JSON_FACTORY.createParser(DOC.getBytes("UTF-8"));
                break;
            case 1:
                jp = JSON_FACTORY.createParser(DOC);
                break;
            case 2: // NEW: let's also exercise UTF-32...
                jp = JSON_FACTORY.createParser(encodeInUTF32BE(DOC));
                break;
            }
            assertToken(JsonToken.START_OBJECT, jp.nextToken());
            assertToken(JsonToken.FIELD_NAME, jp.nextToken());
            assertEquals("doc", jp.getCurrentName());
            assertToken(JsonToken.VALUE_STRING, jp.nextToken());
            
            String act = getAndVerifyText(jp);
            if (act.length() != VALUE.length()) {
                fail("Expected length "+VALUE.length()+", got "+act.length());
            }
            if (!act.equals(VALUE)) {
                fail("Long text differs");
            }

            // should still know the field name
            assertEquals("doc", jp.getCurrentName());
            assertToken(JsonToken.END_OBJECT, jp.nextToken());
            assertNull(jp.nextToken());
            jp.close();
        }
    }
    private void _testHandlingOfInvalidSpace(boolean useStream) throws Exception
    {
        final String JSON = "{ \u00A0 \"a\":1}";
        JsonParser jp = useStream
                ? createParserUsingStream(JSON_FACTORY, JSON, "UTF-8")
                : createParserUsingReader(JSON_FACTORY, JSON);
        assertToken(JsonToken.START_OBJECT, jp.nextToken());
        try {
            jp.nextToken();
            fail("Should have failed");
        } catch (JsonParseException e) {
            verifyException(e, "unexpected character");
            // and correct error code
            verifyException(e, "code 160");
        }
        jp.close();
    }
    private void _testHandlingOfInvalidSpaceFromResource(boolean useStream) throws Exception
    {
        InputStream in = getClass().getResourceAsStream("/test_0xA0.json");
        @SuppressWarnings("resource")
        JsonParser jp = useStream
                ? JSON_FACTORY.createParser(in)
                : JSON_FACTORY.createParser(new InputStreamReader(in, "UTF-8"));
        assertToken(JsonToken.START_OBJECT, jp.nextToken());
        try {
            assertToken(JsonToken.FIELD_NAME, jp.nextToken());
            assertEquals("request", jp.getCurrentName());
            assertToken(JsonToken.START_OBJECT, jp.nextToken());
            assertToken(JsonToken.FIELD_NAME, jp.nextToken());
            assertEquals("mac", jp.getCurrentName());
            assertToken(JsonToken.VALUE_STRING, jp.nextToken());
            assertNotNull(jp.getText());
            assertToken(JsonToken.FIELD_NAME, jp.nextToken());
            assertEquals("data", jp.getCurrentName());
            assertToken(JsonToken.START_OBJECT, jp.nextToken());

            // ... and from there on, just loop
            
            while (jp.nextToken()  != null) { }
            fail("Should have failed");
        } catch (JsonParseException e) {
            verifyException(e, "unexpected character");
            // and correct error code
            verifyException(e, "code 160");
        }
        jp.close();
    }
    @SuppressWarnings("resource")
    private void _testGetValueAsText(JsonFactory f,
            boolean useBytes, boolean delegate) throws Exception
    {
        String JSON = "{\"a\":1,\"b\":true,\"c\":null,\"d\":\"foo\"}";
        JsonParser p = useBytes ? f.createParser(JSON.getBytes("UTF-8"))
                : f.createParser(JSON);

        if (delegate) {
            p = new JsonParserDelegate(p);
        }
        
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        assertNull(p.getValueAsString());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("a", p.getText());
        assertEquals("a", p.getValueAsString());
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals("1", p.getValueAsString());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("b", p.getValueAsString());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertEquals("true", p.getValueAsString());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("c", p.getValueAsString());
        assertToken(JsonToken.VALUE_NULL, p.nextToken());
        // null token returned as Java null, as per javadoc
        assertNull(p.getValueAsString());

        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        assertEquals("d", p.getValueAsString());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        assertEquals("foo", p.getValueAsString());

        assertToken(JsonToken.END_OBJECT, p.nextToken());
        assertNull(p.getValueAsString());

        assertNull(p.nextToken());
        p.close();
    }
    /**********************************************************
     */

    private void doTestSpec(boolean verify) throws IOException
    {
        // First, using a StringReader:
        doTestSpecIndividual(null, verify);

        // Then with streams using supported encodings:
        doTestSpecIndividual("UTF-8", verify);
        doTestSpecIndividual("UTF-16BE", verify);
        doTestSpecIndividual("UTF-16LE", verify);

        /* Hmmh. UTF-32 is harder only because JDK doesn't come with
         * a codec for it. Can't test it yet using this method
         */
        doTestSpecIndividual("UTF-32", verify);
    }
    private void doTestSpecIndividual(String enc, boolean verify) throws IOException
    {
        String doc = SAMPLE_DOC_JSON_SPEC;
        JsonParser jp;

        if (enc == null) {
            jp = createParserUsingReader(doc);
        } else {
            jp = createParserUsingStream(doc, enc);
        }
        verifyJsonSpecSampleDoc(jp, verify);
        jp.close();
    }
    public void testGetValueAsTextBytes() throws Exception
    {
        JsonFactory f = new JsonFactory();
        _testGetValueAsText(f, true, false);
        _testGetValueAsText(f, true, true);
    }
}
