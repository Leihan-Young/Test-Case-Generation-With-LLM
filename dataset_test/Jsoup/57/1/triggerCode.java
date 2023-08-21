package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
public class ElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";
    @Test
    public void testChainedRemoveAttributes() {
        String html = "<a one two three four>Text</a>";
        Document doc = Jsoup.parse(html);
        Element a = doc.select("a").first();
        a
            .removeAttr("zero")
            .removeAttr("one")
            .removeAttr("two")
            .removeAttr("three")
            .removeAttr("four")
            .removeAttr("five");
        assertEquals("<a>Text</a>", a.outerHtml());
    }
}
