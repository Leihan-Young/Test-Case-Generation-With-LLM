package com.fasterxml.jackson.databind.ser;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
/**
 * Unit tests for checking whether JsonSerializerFactory.Feature
 * configuration works
 */
public class TestFeatures
    extends BaseMapTest
{
    public void testVisibilityFeatures() throws Exception
    {
        ObjectMapper om = new ObjectMapper();
        // Only use explicitly specified values to be serialized/deserialized (i.e., JSONProperty).
        om.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
        om.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
        om.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
        om.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
        om.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
        om.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        om.configure(MapperFeature.INFER_PROPERTY_MUTATORS, false);
        om.configure(MapperFeature.USE_ANNOTATIONS, true);

        JavaType javaType = om.getTypeFactory().constructType(TCls.class);        
        BeanDescription desc = (BeanDescription) om.getSerializationConfig().introspect(javaType);
        List<BeanPropertyDefinition> props = desc.findProperties();
        if (props.size() != 1) {
            fail("Should find 1 property, not "+props.size()+"; properties = "+props);
        }
    }
}
