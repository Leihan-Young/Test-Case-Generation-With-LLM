package org.jsoup.nodes;
import org.jsoup.Jsoup;
import org.junit.Test;
import static org.jsoup.nodes.Document.OutputSettings;
import static org.jsoup.nodes.Entities.EscapeMode.*;
import static org.junit.Assert.*;
public class EntitiesTest {
    @Test public void escapesGtInXmlAttributesButNotInHtml() {
        // https://github.com/jhy/jsoup/issues/528 - < is OK in HTML attribute values, but not in XML


        String docHtml = "<a title='<p>One</p>'>One</a>";
        Document doc = Jsoup.parse(docHtml);
        Element element = doc.select("a").first();

        doc.outputSettings().escapeMode(base);
        assertEquals("<a title=\"<p>One</p>\">One</a>", element.outerHtml());

        doc.outputSettings().escapeMode(xhtml);
        assertEquals("<a title=\"&lt;p>One&lt;/p>\">One</a>", element.outerHtml());
    }
}
