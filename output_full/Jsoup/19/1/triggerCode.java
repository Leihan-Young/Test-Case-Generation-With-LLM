package org.jsoup.safety;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests for the cleaner.

 @author Jonathan Hedley, jonathan@hedley.net */
public class CleanerTest {
    @Test public void handlesCustomProtocols() {
        String html = "<img src='cid:12345' /> <img src='data:gzzt' />";
        String dropped = Jsoup.clean(html, Whitelist.basicWithImages());
        assertEquals("<img /> \n<img />", dropped);

        String preserved = Jsoup.clean(html, Whitelist.basicWithImages().addProtocols("img", "src", "cid", "data"));
        assertEquals("<img src=\"cid:12345\" /> \n<img src=\"data:gzzt\" />", preserved);
    }
}
