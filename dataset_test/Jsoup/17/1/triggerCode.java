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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class ParserTest {
    @Test public void handles0CharacterAsText() {
        Document doc = Jsoup.parse("0<p>0</p>");
        assertEquals("0\n<p>0</p>", doc.body().html());
    }
}
