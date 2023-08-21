package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class HtmlParserTest {
    @Test public void preservesSpaceInTextArea() {
        // preserve because the tag is marked as preserve white space
        Document doc = Jsoup.parse("<textarea>\n\tOne\n\tTwo\n\tThree\n</textarea>");
        String expect = "One\n\tTwo\n\tThree"; // the leading and trailing spaces are dropped as a convenience to authors
        Element el = doc.select("textarea").first();
        assertEquals(expect, el.text());
        assertEquals(expect, el.val());
        assertEquals(expect, el.html());
        assertEquals("<textarea>\n\t" + expect + "\n</textarea>", el.outerHtml()); // but preserved in round-trip html
    }
}
