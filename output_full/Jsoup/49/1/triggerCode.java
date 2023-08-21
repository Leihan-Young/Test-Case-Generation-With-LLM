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
    public void appendMustCorrectlyMoveChildrenInsideOneParentElement() {
        Document doc = new Document("");
        Element body = doc.appendElement("body");
        body.appendElement("div1");
        body.appendElement("div2");
        final Element div3 = body.appendElement("div3");
        div3.text("Check");
        final Element div4 = body.appendElement("div4");

        ArrayList<Element> toMove = new ArrayList<Element>();
        toMove.add(div3);
        toMove.add(div4);

        body.insertChildren(0, toMove);

        String result = doc.toString().replaceAll("\\s+", "");
        assertEquals("<body><div3>Check</div3><div4></div4><div1></div1><div2></div2></body>", result);

    }
}
