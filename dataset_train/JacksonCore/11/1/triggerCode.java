package com.fasterxml.jackson.core.sym;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import com.fasterxml.jackson.core.*;
/**
 * Tests that use symbol table functionality through parser.
 */
public class SymbolsViaParserTest
    extends com.fasterxml.jackson.core.BaseTest
{
    /**********************************************************
     */

    private void _test17Chars(boolean useBytes) throws IOException
    {
        String doc = _createDoc17();
        JsonFactory f = new JsonFactory();
        
        JsonParser p = useBytes
                ? f.createParser(doc.getBytes("UTF-8"))
                : f.createParser(doc);
        HashSet<String> syms = new HashSet<String>();
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        for (int i = 0; i < 50; ++i) {
            assertToken(JsonToken.FIELD_NAME, p.nextToken());
            syms.add(p.getCurrentName());
            assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        }
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        assertEquals(50, syms.size());
        p.close();
    }
    private String _createDoc17() {
        StringBuilder sb = new StringBuilder(1000);
        sb.append("{\n");
        for (int i = 1; i <= 50; ++i) {
            if (i > 1) {
                sb.append(",\n");
            }
            sb.append("\"lengthmatters")
                .append(1000 + i)
                .append("\": true");
        }
        sb.append("\n}");
        return sb.toString();
    }
    public void testSymbolTableExpansionBytes() throws Exception {
        _testSymbolTableExpansion(true);
    }
}
