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
    @Test
    public void testInvalidTableContents() throws IOException {
        File in = ParseTest.getFile("/htmltests/table-invalid-elements.html");
        Document doc = Jsoup.parse(in, "UTF-8");
        doc.outputSettings().prettyPrint(true);
        String rendered = doc.toString();
        int endOfEmail = rendered.indexOf("Comment");
        int guarantee = rendered.indexOf("Why am I here?");
        assertTrue("Comment not found", endOfEmail > -1);
        assertTrue("Search text not found", guarantee > -1);
        assertTrue("Search text did not come after comment", guarantee > endOfEmail);
    }
}
