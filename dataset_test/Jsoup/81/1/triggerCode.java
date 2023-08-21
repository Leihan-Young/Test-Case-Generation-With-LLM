package org.jsoup.helper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import static org.jsoup.integration.ParseTest.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class DataUtilTest {
    private InputStream stream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
    private InputStream stream(String data, String charset) {
        try {
            return new ByteArrayInputStream(data.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            fail();
        }
        return null;
    }
    @Test
    public void supportsXmlCharsetDeclaration() throws IOException {
        String encoding = "iso-8859-1";
        InputStream soup = new ByteArrayInputStream((
            "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">Hellö Wörld!</html>"
        ).getBytes(encoding));

        Document doc = Jsoup.parse(soup, null, "");
        assertEquals("Hellö Wörld!", doc.body().text());
    }
}
