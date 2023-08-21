package com.fasterxml.jackson.core.json;
import java.io.ByteArrayOutputStream;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
public class RawValueWithSurrogatesTest
    extends com.fasterxml.jackson.core.BaseTest
{
    private void _testRawWithSurrogatesString(boolean useCharArray) throws Exception
    {
        // boundaries are not exact, may vary, so use this:

        final int OFFSET = 3;
        final int COUNT = 100;

        for (int i = OFFSET; i < COUNT; ++i) {
            StringBuilder sb = new StringBuilder(1000);
            for (int j = 0; j < i; ++j) {
                sb.append(' ');
            }
            sb.append(SURROGATES_307);
            final String text = sb.toString();
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            JsonGenerator g = JSON_F.createGenerator(out);
            if (useCharArray) {
                char[] ch = text.toCharArray();
                g.writeRawValue(ch, OFFSET, ch.length - OFFSET);
            } else {
                g.writeRawValue(text, OFFSET, text.length() - OFFSET);
            }
            g.close();
            byte[] b = out.toByteArray();
            assertNotNull(b);
        }
    }
    private void _testRawWithSurrogatesString(boolean useCharArray) throws Exception
    {
        // boundaries are not exact, may vary, so use this:

        final int OFFSET = 3;
        final int COUNT = 100;

        for (int i = OFFSET; i < COUNT; ++i) {
            StringBuilder sb = new StringBuilder(1000);
            for (int j = 0; j < i; ++j) {
                sb.append(' ');
            }
            sb.append(SURROGATES_307);
            final String text = sb.toString();
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            JsonGenerator g = JSON_F.createGenerator(out);
            if (useCharArray) {
                char[] ch = text.toCharArray();
                g.writeRawValue(ch, OFFSET, ch.length - OFFSET);
            } else {
                g.writeRawValue(text, OFFSET, text.length() - OFFSET);
            }
            g.close();
            byte[] b = out.toByteArray();
            assertNotNull(b);
        }
    }
    public void testRawWithSurrogatesString() throws Exception {
        _testRawWithSurrogatesString(false);
    }
}
