package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
    @Test public void testElementSiblingIndexSameContent() {
        Document doc = Jsoup.parse("<div><p>One</p>...<p>One</p>...<p>One</p>");
        Elements ps = doc.select("p");
        assertTrue(0 == ps.get(0).elementSiblingIndex());
        assertTrue(1 == ps.get(1).elementSiblingIndex());
        assertTrue(2 == ps.get(2).elementSiblingIndex());
    }
}
