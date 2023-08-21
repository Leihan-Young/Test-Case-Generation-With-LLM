package org.jsoup.select;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests that the selector selects correctly.

 @author Jonathan Hedley, jonathan@hedley.net */
public class SelectorTest {
    @Test public void notAll() {
        Document doc = Jsoup.parse("<p>Two</p> <p><span>Three</span></p>");

        Elements el1 = doc.body().select(":not(p)"); // should just be the span
        assertEquals(2, el1.size());
        assertEquals("body", el1.first().tagName());
        assertEquals("span", el1.last().tagName());
    }
}
