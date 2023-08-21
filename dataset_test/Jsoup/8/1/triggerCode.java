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
    @Test public void parentlessToString() {
        Document doc = Jsoup.parse("<img src='foo'>");
        Element img = doc.select("img").first();
        assertEquals("\n<img src=\"foo\" />", img.toString());

        img.remove(); // lost its parent
        assertEquals("<img src=\"foo\" />", img.toString());
    }
}
