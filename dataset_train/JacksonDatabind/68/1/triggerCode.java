package com.fasterxml.jackson.databind.struct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
public class SingleValueAsArrayTest extends BaseMapTest
{
    public void testSuccessfulDeserializationOfObjectWithChainedArrayCreators() throws IOException
    {
        MAPPER.readValue(JSON, Bean1421A.class);
    }
}
