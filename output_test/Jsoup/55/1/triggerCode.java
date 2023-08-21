package org.jsoup.parser;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 Test suite for attribute parser.

 @author Jonathan Hedley, jonathan@hedley.net */
public class AttributeParseTest {
    @Test public void dropsSlashFromAttributeName() {
        String html = "<img /onerror='doMyJob'/>";
        Document doc = Jsoup.parse(html);
        assertTrue("SelfClosingStartTag ignores last character", doc.select("img[onerror]").size() != 0);
        assertEquals("<img onerror=\"doMyJob\">", doc.body().html());

        doc = Jsoup.parse(html, "", Parser.xmlParser());
        assertEquals("<img onerror=\"doMyJob\" />", doc.html());
    }
}
