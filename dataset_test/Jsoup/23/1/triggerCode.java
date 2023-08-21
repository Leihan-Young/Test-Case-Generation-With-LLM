package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jsoup.nodes.Entities;
import java.nio.charset.Charset;
public class EntitiesTest {
    @Test public void letterDigitEntities() {
        String html = "<p>&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;</p>";
        Document doc = Jsoup.parse(html);
        Element p = doc.select("p").first();
        assertEquals("&sup1;&sup2;&sup3;&frac14;&frac12;&frac34;", p.html());
        assertEquals("¹²³¼½¾", p.text());
    }
}
