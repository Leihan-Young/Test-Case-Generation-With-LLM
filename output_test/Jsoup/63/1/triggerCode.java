package org.jsoup.parser;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.*;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
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
    @Test public void selfClosingOnNonvoidIsError() {
        String html = "<p>test</p><div /><div>Two</div>";
        Parser parser = Parser.htmlParser().setTrackErrors(5);
        parser.parseInput(html, "");
        assertEquals(1, parser.getErrors().size());
        assertEquals("18: Tag cannot be self closing; not a void tag", parser.getErrors().get(0).toString());

        assertFalse(Jsoup.isValid(html, Whitelist.relaxed()));
        String clean = Jsoup.clean(html, Whitelist.relaxed());
        assertEquals("<p>test</p> <div></div> <div> Two </div>", StringUtil.normaliseWhitespace(clean));
    }
}
