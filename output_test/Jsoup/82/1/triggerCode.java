package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
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
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class HtmlParserTest {
    @Test public void fallbackToUtfIfCantEncode() throws IOException {
        // that charset can't be encoded, so make sure we flip to utf

        String in = "<html><meta charset=\"ISO-2022-CN\"/>One</html>";
        Document doc = Jsoup.parse(new ByteArrayInputStream(in.getBytes()), null, "");

        assertEquals("UTF-8", doc.charset().name());
        assertEquals("One", doc.text());

        String html = doc.outerHtml();
        assertEquals("<html><head><meta charset=\"UTF-8\"></head><body>One</body></html>", TextUtil.stripNewlines(html));
    }
}
