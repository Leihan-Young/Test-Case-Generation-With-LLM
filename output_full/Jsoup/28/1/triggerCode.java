package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.charset.Charset;
public class EntitiesTest {
    @Test public void unescape() {
        String text = "Hello &amp;&LT&gt; &reg &angst; &angst &#960; &#960 &#x65B0; there &! &frac34; &copy; &COPY;";
        assertEquals("Hello &<> ® Å &angst π π 新 there &! ¾ © ©", Entities.unescape(text));

        assertEquals("&0987654321; &unknown", Entities.unescape("&0987654321; &unknown"));
    }
}
