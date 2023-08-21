package org.jsoup.safety;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests for the cleaner.

 @author Jonathan Hedley, jonathan@hedley.net */
public class CleanerTest {
    @Test public void handlesFramesets() {
        String dirty = "<html><head><script></script><noscript></noscript></head><frameset><frame src=\"foo\" /><frame src=\"foo\" /></frameset></html>";
        String clean = Jsoup.clean(dirty, Whitelist.basic());
        assertEquals("", clean); // nothing good can come out of that

        Document dirtyDoc = Jsoup.parse(dirty);
        Document cleanDoc = new Cleaner(Whitelist.basic()).clean(dirtyDoc);
        assertFalse(cleanDoc == null);
        assertEquals(0, cleanDoc.body().childNodes().size());
    }
}
