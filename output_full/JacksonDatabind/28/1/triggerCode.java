package com.fasterxml.jackson.databind.node;
import java.math.BigDecimal;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
/**
 * Additional tests for {@link ObjectNode} container class.
 */
public class TestObjectNode
    extends BaseMapTest
{
    public void testIssue941() throws Exception
    {
        ObjectNode object = MAPPER.createObjectNode();

        String json = MAPPER.writeValueAsString(object);
        System.out.println("json: "+json);

        ObjectNode de1 = MAPPER.readValue(json, ObjectNode.class);  // this works
        System.out.println("Deserialized to ObjectNode: "+de1);

        MyValue de2 = MAPPER.readValue(json, MyValue.class);  // but this throws exception
        System.out.println("Deserialized to MyValue: "+de2);
    }
}
