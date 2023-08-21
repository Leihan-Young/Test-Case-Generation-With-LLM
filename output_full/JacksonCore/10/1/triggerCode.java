package com.fasterxml.jackson.core.sym;
import java.io.*;
import java.lang.reflect.Field;
import com.fasterxml.jackson.core.*;
/**
 * Unit test(s) to verify that handling of (byte-based) symbol tables
 * is working.
 */
public class TestByteBasedSymbols
    extends com.fasterxml.jackson.core.BaseTest
{
    private String createDoc(String[] fieldNames, boolean add)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        int len = fieldNames.length;
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('"');
            sb.append(add ? fieldNames[i] : fieldNames[len - (i+1)]);
            sb.append("\" : ");
            sb.append(i);
        }
        sb.append(" }");
        return sb.toString();
    }
    public void testIssue207() throws Exception
    {
        ByteQuadsCanonicalizer nc = ByteQuadsCanonicalizer.createRoot(-523743345);
        Field byteSymbolCanonicalizerField = JsonFactory.class.getDeclaredField("_byteSymbolCanonicalizer");
        byteSymbolCanonicalizerField.setAccessible(true);
        JsonFactory jsonF = new JsonFactory();
        byteSymbolCanonicalizerField.set(jsonF, nc);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        stringBuilder.append("    \"expectedGCperPosition\": null");
        for (int i = 0; i < 60; ++i) {
            stringBuilder.append(",\n    \"").append(i + 1).append("\": null");
        }
        stringBuilder.append("\n}");

        JsonParser p = jsonF.createParser(stringBuilder.toString().getBytes("UTF-8"));
        while (p.nextToken() != null) { }
        p.close();
    }
}
