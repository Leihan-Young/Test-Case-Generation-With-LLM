package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.internal.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 * Tests for the Parser
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class HtmlParserTest {
    @Test public void retainsAttributesOfDifferentCaseIfSensitive() {
        String html = "<p One=One One=Two one=Three two=Four two=Five Two=Six>Text</p>";
        Parser parser = Parser.htmlParser().settings(ParseSettings.preserveCase);
        Document doc = parser.parseInput(html, "");
        assertEquals("<p One=\"One\" one=\"Three\" two=\"Four\" Two=\"Six\">Text</p>", doc.selectFirst("p").outerHtml());
    }
}
