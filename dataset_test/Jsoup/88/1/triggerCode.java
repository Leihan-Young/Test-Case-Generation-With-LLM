package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class AttributeTest {
    @Test public void booleanAttributesAreEmptyStringValues() {
        Document doc = Jsoup.parse("<div hidden>");
        Attributes attributes = doc.body().child(0).attributes();
        assertEquals("", attributes.get("hidden"));

        Attribute first = attributes.iterator().next();
        assertEquals("hidden", first.getKey());
        assertEquals("", first.getValue());
    }
}
