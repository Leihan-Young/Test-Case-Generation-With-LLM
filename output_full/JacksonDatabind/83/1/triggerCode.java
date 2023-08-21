package com.fasterxml.jackson.databind.filter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/**
 * Tests to exercise handler methods of {@link DeserializationProblemHandler}.
 *
 * @since 2.8
 */
public class ProblemHandlerTest extends BaseMapTest
{
    public void testWeirdStringHandling() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
            .addHandler(new WeirdStringHandler(SingleValuedEnum.A))
            ;
        SingleValuedEnum result = mapper.readValue("\"B\"", SingleValuedEnum.class);
        assertEquals(SingleValuedEnum.A, result);

        // also, write [databind#1629] try this
        mapper = new ObjectMapper()
                .addHandler(new WeirdStringHandler(null));
        UUID result2 = mapper.readValue(quote("not a uuid!"), UUID.class);
        assertNull(result2);
    }
}
