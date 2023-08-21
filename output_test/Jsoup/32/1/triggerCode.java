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
    @Test public void testClonesClassnames() {
        Document doc = Jsoup.parse("<div class='one two'></div>");
        Element div = doc.select("div").first();
        Set<String> classes = div.classNames();
        assertEquals(2, classes.size());
        assertTrue(classes.contains("one"));
        assertTrue(classes.contains("two"));

        Element copy = div.clone();
        Set<String> copyClasses = copy.classNames();
        assertEquals(2, copyClasses.size());
        assertTrue(copyClasses.contains("one"));
        assertTrue(copyClasses.contains("two"));
        copyClasses.add("three");
        copyClasses.remove("one");

        assertTrue(classes.contains("one"));
        assertFalse(classes.contains("three"));
        assertFalse(copyClasses.contains("one"));
        assertTrue(copyClasses.contains("three"));

        assertEquals("", div.html());
        assertEquals("", copy.html());
    }
}
