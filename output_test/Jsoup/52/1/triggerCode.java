package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.junit.Ignore;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    public void testMetaCharsetUpdateXmlNoCharset() {
        final Document doc = createXmlDocument("1.0", "none", false);
        doc.updateMetaCharsetElement(true);
        doc.charset(Charset.forName(charsetUtf8));
        
        final String xmlCharsetUTF8 = "<?xml version=\"1.0\" encoding=\"" + charsetUtf8 + "\"?>\n" +
                                        "<root>\n" +
                                        " node\n" +
                                        "</root>";
        assertEquals(xmlCharsetUTF8, doc.toString());
        
        XmlDeclaration selectedNode = (XmlDeclaration) doc.childNode(0);
        assertEquals(charsetUtf8, selectedNode.attr("encoding"));
    }
}
