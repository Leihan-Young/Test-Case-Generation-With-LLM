package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class HtmlParserTest {
    @Test public void caseSensitiveParseTree() {
        String html = "<r><X>A</X><y>B</y></r>";
        Parser parser = Parser.htmlParser();
        parser.settings(ParseSettings.preserveCase);
        Document doc = parser.parseInput(html, "");
        assertEquals("<r> <X> A </X> <y> B </y> </r>", StringUtil.normaliseWhitespace(doc.body().html()));

    }
}
