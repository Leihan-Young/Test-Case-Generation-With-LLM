package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests for the DocumentType node
 *
 * @author Jonathan Hedley, http://jonathanhedley.com/
 */
public class DocumentTypeTest {
    private String htmlOutput(String in) {
        DocumentType type = (DocumentType) Jsoup.parse(in).childNode(0);
        return type.outerHtml();
    }
    private String xmlOutput(String in) {
        return Jsoup.parse(in, "", Parser.xmlParser()).childNode(0).outerHtml();
    }
    @Test public void testRoundTrip() {
        String base = "<!DOCTYPE html>";
        assertEquals("<!doctype html>", htmlOutput(base));
        assertEquals(base, xmlOutput(base));

        String publicDoc = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        assertEquals(publicDoc, htmlOutput(publicDoc));
        assertEquals(publicDoc, xmlOutput(publicDoc));

        String systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">";
        assertEquals(systemDoc, htmlOutput(systemDoc));
        assertEquals(systemDoc, xmlOutput(systemDoc));

        String legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">";
        assertEquals(legacyDoc, htmlOutput(legacyDoc));
        assertEquals(legacyDoc, xmlOutput(legacyDoc));
    }
}
