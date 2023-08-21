package com.fasterxml.jackson.dataformat.xml.misc;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlTestBase;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
public class XmlTextTest extends XmlTestBase
{
    public void testMixedContent() throws Exception
    {
        WindSpeed result = MAPPER.readValue("<windSpeed units='kt'> 27 <radius>20</radius></windSpeed>",
                WindSpeed.class);
        assertEquals(27, result.value);
        assertNotNull(result.radius);
        assertEquals(20, result.radius.value);
    }
}
