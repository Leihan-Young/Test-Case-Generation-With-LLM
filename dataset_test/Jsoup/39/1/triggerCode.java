package org.jsoup.helper;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import static org.junit.Assert.assertEquals;
public class DataUtilTest {
    @Test public void discardsSpuriousByteOrderMarkWhenNoCharsetSet() {
        String html = "\uFEFF<html><head><title>One</title></head><body>Two</body></html>";
        ByteBuffer buffer = Charset.forName("UTF-8").encode(html);
        Document doc = DataUtil.parseByteData(buffer, null, "http://foo.com/", Parser.htmlParser());
        assertEquals("One", doc.head().text());
        assertEquals("UTF-8", doc.outputSettings().charset().displayName());
    }
}
