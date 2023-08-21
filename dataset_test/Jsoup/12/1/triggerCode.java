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
    @Test public void testByAttributeRegexCombined() {
        Document doc = Jsoup.parse("<div><table class=x><td>Hello</td></table></div>");
        Elements els = doc.select("div table[class~=x|y]");
        assertEquals(1, els.size());
        assertEquals("Hello", els.text());
    }
}
