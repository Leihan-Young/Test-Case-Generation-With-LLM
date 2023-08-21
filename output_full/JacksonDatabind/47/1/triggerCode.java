package com.fasterxml.jackson.databind.ser;
import java.io.IOException;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * This unit test suite tests use of @JsonClass Annotation
 * with bean serialization.
 */
public class TestJsonSerialize
    extends BaseMapTest
{
    public void testBrokenAnnotation() throws Exception
    {
        try {
            serializeAsString(MAPPER, new BrokenClass());
        } catch (Exception e) {
            verifyException(e, "types not related");
        }
    }
}
