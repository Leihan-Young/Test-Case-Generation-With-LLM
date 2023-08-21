package org.jsoup.safety;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests for the cleaner.

 @author Jonathan Hedley, jonathan@hedley.net */
public class CleanerTest {
    @Test public void testIsValid() {
        String ok = "<p>Test <b><a href='http://example.com/'>OK</a></b></p>";
        String nok1 = "<p><script></script>Not <b>OK</b></p>";
        String nok2 = "<p align=right>Test Not <b>OK</b></p>";
        String nok3 = "<!-- comment --><p>Not OK</p>"; // comments and the like will be cleaned
        assertTrue(Jsoup.isValid(ok, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok1, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok2, Whitelist.basic()));
        assertFalse(Jsoup.isValid(nok3, Whitelist.basic()));
    }
}
