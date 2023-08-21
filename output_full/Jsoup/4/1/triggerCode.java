package org.jsoup.nodes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jsoup.nodes.Entities;
import java.nio.charset.Charset;
public class EntitiesTest {
    @Test public void escape() {
        String text = "Hello &<> Å å π 新 there";
        String escapedAscii = Entities.escape(text, Charset.forName("ascii").newEncoder(), Entities.EscapeMode.base);
        String escapedAsciiFull = Entities.escape(text, Charset.forName("ascii").newEncoder(), Entities.EscapeMode.extended);
        String escapedUtf = Entities.escape(text, Charset.forName("UTF-8").newEncoder(), Entities.EscapeMode.base);

        assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; &#960; &#26032; there", escapedAscii);
        assertEquals("Hello &amp;&lt;&gt; &angst; &aring; &pi; &#26032; there", escapedAsciiFull);
        assertEquals("Hello &amp;&lt;&gt; &Aring; &aring; π 新 there", escapedUtf);
        // odd that it's defined as aring in base but angst in full
    }
}
