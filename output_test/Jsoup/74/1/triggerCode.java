package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
/**
 * Tests for Element (DOM stuff mostly).
 *
 * @author Jonathan Hedley
 */
public class ElementTest {
    private String reference = "<div id=div1><p>Hello</p><p>Another <b>element</b></p><div id=div2><img src=foo.png></div></div>";
    @Test public void testNormalizesInvisiblesInText() {
        // return Character.getType(c) == 16 && (c == 8203 || c == 8204 || c == 8205 || c == 173);
        String escaped = "This&shy;is&#x200b;one&#x200c;long&#x200d;word";
        String decoded = "This\u00ADis\u200Bone\u200Clong\u200Dword"; // browser would not display those soft hyphens / other chars, so we don't want them in the text

        Document doc = Jsoup.parse("<p>" + escaped);
        Element p = doc.select("p").first();
        doc.outputSettings().charset("ascii"); // so that the outer html is easier to see with escaped invisibles
        assertEquals("Thisisonelongword", p.text()); // text is normalized
        assertEquals("<p>" + escaped + "</p>", p.outerHtml()); // html / whole text keeps &shy etc;
        assertEquals(decoded, p.textNodes().get(0).getWholeText());

        Element matched = doc.select("p:contains(Thisisonelongword)").first(); // really just oneloneword, no invisibles
        assertEquals("p", matched.nodeName());
        assertTrue(matched.is(":containsOwn(Thisisonelongword)"));

    }
}
