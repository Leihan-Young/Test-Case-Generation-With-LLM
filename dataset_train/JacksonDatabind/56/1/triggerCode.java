package com.fasterxml.jackson.databind.deser;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
public class TestJdkTypes extends BaseMapTest
{
    private static String asArray(Object value) {
        final String stringVal = value.toString();
        return new StringBuilder(stringVal.length() + 2).append("[").append(stringVal).append("]").toString();
    }
    public void testLocale() throws IOException
    {
        assertEquals(new Locale("en"), MAPPER.readValue(quote("en"), Locale.class));
        assertEquals(new Locale("es", "ES"), MAPPER.readValue(quote("es_ES"), Locale.class));
        assertEquals(new Locale("FI", "fi", "savo"), MAPPER.readValue(quote("fi_FI_savo"), Locale.class));
        assertEquals(new Locale("en", "US"), MAPPER.readValue(quote("en-US"), Locale.class));

        // [databind#1123]
        Locale loc = MAPPER.readValue(quote(""), Locale.class);
        assertSame(Locale.ROOT, loc);
    }
}
