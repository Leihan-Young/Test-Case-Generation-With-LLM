package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class HtmlParserTest {
    @Test public void handlesQuotesInCommentsInScripts() {
        String html = "<script>\n" +
                "  <!--\n" +
                "    document.write('</scr' + 'ipt>');\n" +
                "  // -->\n" +
                "</script>";
        Document node = Jsoup.parseBodyFragment(html);
        assertEquals("<script>\n" + "  <!--\n" + "    document.write('</scr' + 'ipt>');\n" + "  // -->\n" + "</script>", node.body().html());
    }
}
