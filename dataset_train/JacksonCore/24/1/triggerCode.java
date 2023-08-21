package com.fasterxml.jackson.core.json.async;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.async.AsyncTestBase;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.testsupport.AsyncReaderWrapper;
import com.fasterxml.jackson.core.JsonToken;
public class AsyncNumberCoercionTest extends AsyncTestBase
{
    private final JsonFactory JSON_F = new JsonFactory();
    private AsyncReaderWrapper createParser(String doc) throws IOException
    {
        return asyncForBytes(JSON_F, 1, _jsonDoc(doc), 1);
    }
    public void testToLongFailing() throws Exception
    {
        AsyncReaderWrapper p;

        // BigInteger -> error
        BigInteger big = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN);
        p = createParser(String.valueOf(big));
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
        assertEquals(big, p.getBigIntegerValue());
        assertEquals(big, p.getNumberValue());
        try {
            p.getLongValue();
            fail("Should not pass");
        } catch (InputCoercionException e) {
            verifyException(e, "out of range of long");
            assertEquals(JsonToken.VALUE_NUMBER_INT, e.getInputType());
            assertEquals(Long.TYPE, e.getTargetType());
        }
        BigInteger small = BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.TEN);
        p = createParser(String.valueOf(small));
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(small, p.getBigIntegerValue());
        try {
            p.getLongValue();
            fail("Should not pass");
        } catch (InputCoercionException e) {
            verifyException(e, "out of range of long");
            assertEquals(JsonToken.VALUE_NUMBER_INT, e.getInputType());
            assertEquals(Long.TYPE, e.getTargetType());
        }
    }
}
