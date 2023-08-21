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
    @Test public void testSupportsNonAsciiTags() {
        String body = "<進捗推移グラフ>Yes</進捗推移グラフ>";
        Document doc = Jsoup.parse(body);
        Elements els = doc.select("進捗推移グラフ");
        assertEquals("Yes", els.text());
    }
}
