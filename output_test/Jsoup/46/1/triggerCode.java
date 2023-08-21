package org.jsoup.nodes;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
/**
 Tests for Document.

 @author Jonathan Hedley, jonathan@hedley.net */
public class DocumentTest {
    private static final String charsetUtf8 = "UTF-8";
    private static final String charsetIso8859 = "ISO-8859-1";
    private Document createHtmlDocument(String charset) {
        final Document doc = Document.createShell("");
        doc.head().appendElement("meta").attr("charset", charset);
        doc.head().appendElement("meta").attr("name", "charset").attr("content", charset);
        
        return doc;
    }
    private Document createXmlDocument(String version, String charset, boolean addDecl) {
        final Document doc = new Document("");
        doc.appendElement("root").text("node");
        doc.outputSettings().syntax(Syntax.xml);
        
        if( addDecl == true ) {
            XmlDeclaration decl = new XmlDeclaration("xml", "", false);
            decl.attr("version", version);
            decl.attr("encoding", charset);
            doc.prependChild(decl);
        }
        
        return doc;
    }
    @Test
    public void testShiftJisRoundtrip() throws Exception {
        String input =
                "<html>"
                        +   "<head>"
                        +     "<meta http-equiv=\"content-type\" content=\"text/html; charset=Shift_JIS\" />"
                        +   "</head>"
                        +   "<body>"
                        +     "before&nbsp;after"
                        +   "</body>"
                        + "</html>";
        InputStream is = new ByteArrayInputStream(input.getBytes(Charset.forName("ASCII")));

        Document doc = Jsoup.parse(is, null, "http://example.com");
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

        String output = new String(doc.html().getBytes(doc.outputSettings().charset()), doc.outputSettings().charset());

        assertFalse("Should not have contained a '?'.", output.contains("?"));
        assertTrue("Should have contained a '&#xa0;' or a '&nbsp;'.", output.contains("&#xa0;") || output.contains("&nbsp;"));
    }
}
