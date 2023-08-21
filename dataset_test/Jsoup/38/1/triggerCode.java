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
    @Test public void convertsImageToImg() {
        // image to img, unless in a svg. old html cruft.
        String h = "<body><image><svg><image /></svg></body>";
        Document doc = Jsoup.parse(h);
        assertEquals("<img />\n<svg>\n <image />\n</svg>", doc.body().html());
    }
}
