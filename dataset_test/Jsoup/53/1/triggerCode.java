package org.jsoup.select;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests that the selector selects correctly.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class SelectorTest {
    @Test public void attributeWithBrackets() {
        String html = "<div data='End]'>One</div> <div data='[Another)]]'>Two</div>";
        Document doc = Jsoup.parse(html);
        assertEquals("One", doc.select("div[data='End]'").first().text());
        assertEquals("Two", doc.select("div[data='[Another)]]'").first().text());
    }
}
