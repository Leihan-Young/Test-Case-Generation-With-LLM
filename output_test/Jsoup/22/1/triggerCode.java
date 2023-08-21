package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Set;
import java.util.Map;
/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
public class ElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";
    @Test public void elementIsNotASiblingOfItself() {
        Document doc = Jsoup.parse("<div><p>One<p>Two<p>Three</div>");
        Element p2 = doc.select("p").get(1);

        assertEquals("Two", p2.text());
        Elements els = p2.siblingElements();
        assertEquals(2, els.size());
        assertEquals("<p>One</p>", els.get(0).outerHtml());
        assertEquals("<p>Three</p>", els.get(1).outerHtml());
    }
}
