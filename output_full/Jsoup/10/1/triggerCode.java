package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.parser.Tag;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests Nodes

 @author Jonathan Hedley, jonathan@hedley.net */
public class NodeTest {
    /*
    Test for an issue with Java's abs URL handler.
     */
    @Test public void absHandlesRelativeQuery() {
        Document doc = Jsoup.parse("<a href='?foo'>One</a> <a href='bar.html?foo'>Two</a>", "http://jsoup.org/path/file?bar");

        Element a1 = doc.select("a").first();
        assertEquals("http://jsoup.org/path/file?foo", a1.absUrl("href"));

        Element a2 = doc.select("a").get(1);
        assertEquals("http://jsoup.org/path/bar.html?foo", a2.absUrl("href"));
    }
}
