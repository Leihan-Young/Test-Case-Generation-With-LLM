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
    @Test public void createsStructureFromBodySnippet() {
        // the bar baz stuff naturally goes into the body, but the 'foo' goes into root, and the normalisation routine
        // needs to move into the start of the body
        String html = "foo <b>bar</b> baz";
        Document doc = Jsoup.parse(html);
        assertEquals ("foo bar baz", doc.text());

    }
}
