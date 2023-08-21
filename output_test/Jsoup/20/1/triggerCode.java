package org.jsoup.helper;
import static org.junit.Assert.assertEquals;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
public class DataUtilTest {
    @Test public void discardsSpuriousByteOrderMark() {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        ByteBuffer buffer = Charset.forName("UTF-8").encode(html);
        Document doc = DataUtil.parseByteData(buffer, "UTF-8", "http://foo.com/", Parser.htmlParser());
        assertEquals("One", doc.head().text());
    }
}
