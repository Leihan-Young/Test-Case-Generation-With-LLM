package com.fasterxml.jackson.core.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import com.fasterxml.jackson.core.*;
public class TestDefaultPrettyPrinter extends BaseTest
{
    private final JsonFactory JSON_F = new JsonFactory();
    private String _printTestData(PrettyPrinter pp, boolean useBytes) throws IOException
    {
        JsonGenerator gen;
        StringWriter sw;
        ByteArrayOutputStream bytes;

        if (useBytes) {
            sw = null;
            bytes = new ByteArrayOutputStream();
            gen = JSON_F.createGenerator(bytes);
        } else {
            sw = new StringWriter();
            bytes = null;
            gen = JSON_F.createGenerator(sw);
        }
        gen.setPrettyPrinter(pp);
        gen.writeStartObject();
        gen.writeFieldName("name");
        gen.writeString("John Doe");
        gen.writeFieldName("age");
        gen.writeNumber(3.14);
        gen.writeEndObject();
        gen.close();

        if (useBytes) {
            return bytes.toString("UTF-8");
        }
        return sw.toString();
    }
    public void testInvalidSubClass() throws Exception
    {
        DefaultPrettyPrinter pp = new MyPrettyPrinter();
        try {
            pp.createInstance();
            fail("Should not pass");
        } catch (IllegalStateException e) {
            verifyException(e, "does not override");
        }
    }
}
