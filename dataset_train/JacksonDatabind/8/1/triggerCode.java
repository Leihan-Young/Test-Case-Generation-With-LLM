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
    public void testStringBuilder() throws Exception
    {
        StringBuilder sb = MAPPER.readValue(quote("abc"), StringBuilder.class);
        assertEquals("abc", sb.toString());
    }
}
