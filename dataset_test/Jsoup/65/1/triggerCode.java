package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class HtmlParserTest {
  @Test public void testTemplateInsideTable() throws IOException {
        File in = ParseTest.getFile("/htmltests/table-polymer-template.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        doc.outputSettings().prettyPrint(true);

        Elements templates = doc.body().getElementsByTag("template");
        for (Element template : templates) {
            assertTrue(template.childNodes().size() > 1);
        }
  }
}
