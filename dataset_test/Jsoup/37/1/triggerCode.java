package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
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
    @Test public void testNotPretty() {
        Document doc = Jsoup.parse("<div>   \n<p>Hello\n there\n</p></div>");
        doc.outputSettings().prettyPrint(false);
        assertEquals("<html><head></head><body><div>   \n<p>Hello\n there\n</p></div></body></html>", doc.html());

        Element div = doc.select("div").first();
        assertEquals("   \n<p>Hello\n there\n</p>", div.html());
    }
}
