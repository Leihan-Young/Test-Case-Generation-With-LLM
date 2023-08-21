package com.fasterxml.jackson.dataformat.xml;
import java.io.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;
import com.fasterxml.jackson.dataformat.xml.util.XmlRootNameLookup;
public class MapperCopyTest extends XmlTestBase
{
    public void testCopyWith() throws Exception
    {
        XmlMapper xmlMapper = newMapper();
        final ObjectMapper xmlMapperNoAnno = xmlMapper.copy()
                .disable(MapperFeature.USE_ANNOTATIONS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        String xml1 = xmlMapper.writeValueAsString(new Pojo282());
        String xml2 = xmlMapperNoAnno.writeValueAsString(new Pojo282());

        if (!xml1.contains("AnnotatedName")) {
            fail("Should use name 'AnnotatedName', xml = "+xml1);
        }
        if (!xml2.contains("Pojo282")
                || xml2.contains("AnnotatedName")) {
            fail("Should NOT use name 'AnnotatedName' but 'Pojo282', xml = "+xml1);
        }
    }
}
