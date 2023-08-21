package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class AttributeTest {
    @Test public void settersOnOrphanAttribute() {
        Attribute attr = new Attribute("one", "two");
        attr.setKey("three");
        String oldVal = attr.setValue("four");
        assertEquals("two", oldVal);
        assertEquals("three", attr.getKey());
        assertEquals("four", attr.getValue());
        assertEquals(null, attr.parent);
    }
}
