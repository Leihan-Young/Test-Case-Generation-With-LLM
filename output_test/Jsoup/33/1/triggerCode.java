package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;
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
    @Test public void handlesKnownEmptyBlocks() {
        // if a known tag, allow self closing outside of spec, but force an end tag. unknown tags can be self closing.
        String h = "<div id='1' /><script src='/foo' /><div id=2><img /><img></div><a id=3 /><i /><foo /><foo>One</foo> <hr /> hr text <hr> hr text two";
        Document doc = Jsoup.parse(h);
        assertEquals("<div id=\"1\"></div><script src=\"/foo\"></script><div id=\"2\"><img /><img /></div><a id=\"3\"></a><i></i><foo /><foo>One</foo> <hr /> hr text <hr /> hr text two", TextUtil.stripNewlines(doc.body().html()));
    }
}
