package org.jsoup.select;
import org.jsoup.MultiLocaleRule;
import org.jsoup.MultiLocaleRule.MultiLocaleTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests that the selector selects correctly.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class SelectorTest {
    @Rule public MultiLocaleRule rule = new MultiLocaleRule();
    @Test public void splitOnBr() {
        String html = "<div><p>One<br>Two<br>Three</p></div>";
        Document doc = Jsoup.parse(html);

        Elements els = doc.select("p:matchText");
        assertEquals(3, els.size());
        assertEquals("One", els.get(0).text());
        assertEquals("Two", els.get(1).text());
        assertEquals("Three", els.get(2).toString());
    }
}
