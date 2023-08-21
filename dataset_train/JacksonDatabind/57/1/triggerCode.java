package com.fasterxml.jackson.databind.seq;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
public class ReadValuesTest extends BaseMapTest
{
    private <T> MappingIterator<T> _iterator(ObjectReader r,
            String json,
            Source srcType) throws IOException
    {
        switch (srcType) {
        case BYTE_ARRAY:
            return r.readValues(json.getBytes("UTF-8"));
        case BYTE_ARRAY_OFFSET:
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(json.getBytes("UTF-8"));
                out.write(0);
                out.write(0);
                out.write(0);
                byte[] b = out.toByteArray();
                return r.readValues(b, 3, b.length-6);
            }
        case INPUT_STREAM:
            return r.readValues(new ByteArrayInputStream(json.getBytes("UTF-8")));
        case READER:
            return r.readValues(new StringReader(json));
        case STRING:
        default:
            return r.readValues(json);
        }
    }
    private void _testRootBeans(Source srcType) throws Exception
    {
        final String JSON = "{\"a\":3}{\"a\":27}  ";

        MappingIterator<Bean> it = _iterator(MAPPER.readerFor(Bean.class),
                JSON, srcType);
                MAPPER.readerFor(Bean.class).readValues(JSON);
        assertNotNull(it.getCurrentLocation());
        assertTrue(it.hasNext());
        Bean b = it.next();
        assertEquals(3, b.a);
        assertTrue(it.hasNext());
        b = it.next();
        assertEquals(27, b.a);
        assertFalse(it.hasNext());
        it.close();

        // Also, test 'readAll()'
        it = MAPPER.readerFor(Bean.class).readValues(JSON);
        List<Bean> all = it.readAll();
        assertEquals(2, all.size());
        it.close();

        it = MAPPER.readerFor(Bean.class).readValues("{\"a\":3}{\"a\":3}");
        Set<Bean> set = it.readAll(new HashSet<Bean>());
        assertEquals(HashSet.class, set.getClass());
        assertEquals(1, set.size());
        assertEquals(3, set.iterator().next().a);
    }
    private void _testRootBeans(Source srcType) throws Exception
    {
        final String JSON = "{\"a\":3}{\"a\":27}  ";

        MappingIterator<Bean> it = _iterator(MAPPER.readerFor(Bean.class),
                JSON, srcType);
                MAPPER.readerFor(Bean.class).readValues(JSON);
        assertNotNull(it.getCurrentLocation());
        assertTrue(it.hasNext());
        Bean b = it.next();
        assertEquals(3, b.a);
        assertTrue(it.hasNext());
        b = it.next();
        assertEquals(27, b.a);
        assertFalse(it.hasNext());
        it.close();

        // Also, test 'readAll()'
        it = MAPPER.readerFor(Bean.class).readValues(JSON);
        List<Bean> all = it.readAll();
        assertEquals(2, all.size());
        it.close();

        it = MAPPER.readerFor(Bean.class).readValues("{\"a\":3}{\"a\":3}");
        Set<Bean> set = it.readAll(new HashSet<Bean>());
        assertEquals(HashSet.class, set.getClass());
        assertEquals(1, set.size());
        assertEquals(3, set.iterator().next().a);
    }
    public void testRootBeans() throws Exception
    {
        for (Source src : Source.values()) {
            _testRootBeans(src);
        }
    }
}
