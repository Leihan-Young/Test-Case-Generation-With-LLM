package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class ParserTest {
    @Test public void parsesQuiteRoughAttributes() {
        String html = "<p =a>One<a =a";
        Document doc = Jsoup.parse(html);
        assertEquals("<p>One<a></a></p>", doc.body().html());
        
        doc = Jsoup.parse("<p .....");
        assertEquals("<p></p>", doc.body().html());
        
        doc = Jsoup.parse("<p .....<p!!");
        assertEquals("<p></p>\n<p></p>", doc.body().html());
    }
}
