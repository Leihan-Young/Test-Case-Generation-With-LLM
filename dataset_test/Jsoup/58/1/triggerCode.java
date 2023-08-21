package org.jsoup.safety;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests for the cleaner.

 @author Jonathan Hedley, jonathan@hedley.net */
public class CleanerTest {
    @Test public void testIsValidBodyHtml() {
        String ok = "<p>Test <b><a href='http://example.com/' rel='nofollow'>OK</a></b></p>";
        String ok1 = "<p>Test <b><a href='http://example.com/'>OK</a></b></p>"; // missing enforced is OK because still needs run thru cleaner
        String nok1 = "<p><script></script>Not <b>OK</b></p>";
        String nok2 = "<p align=right>Test Not <b>OK</b></p>";
        String nok3 = "<!-- comment --><p>Not OK</p>"; // comments and the like will be cleaned
        String nok4 = "<html><head>Foo</head><body><b>OK</b></body></html>"; // not body html
        String nok5 = "<p>Test <b><a href='http://example.com/' rel='nofollowme'>OK</a></b></p>";
        String nok6 = "<p>Test <b><a href='http://example.com/'>OK</b></p>"; // missing close tag
        String nok7 = "</div>What";
        assertTrue(Jsoup.isValid(ok, Whitelist.basic()));
        assertTrue(Jsoup.isValid(ok1, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok1, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok2, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok3, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok4, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok5, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok6, Whitelist.basic()));
        assertFalse(Jsoup.isValid(ok, Whitelist.none()));
        assertFalse(Jsoup.isValid(nok7, Whitelist.basic()));
    }
}
