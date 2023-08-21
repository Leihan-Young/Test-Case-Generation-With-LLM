package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Tests for Document.

 @author Jonathan Hedley, jonathan@hedley.net */
public class DocumentTest {
    @Test public void testNormalisesStructure() {
        Document doc = Jsoup.parse("<html><head><script>one</script><noscript><p>two</p></noscript></head><body><p>three</p></body></html>");
        assertEquals("<html><head><script>one</script><noscript></noscript></head><body><p>two</p><p>three</p></body></html>", TextUtil.stripNewlines(doc.html()));
    }
}
