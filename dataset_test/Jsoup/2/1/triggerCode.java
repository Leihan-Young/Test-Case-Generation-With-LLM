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
import static org.junit.Assert.*;
/**
 Tests for the Parser

 @author Jonathan Hedley, jonathan@hedley.net */
public class ParserTest {
    @Test public void handlesTextAfterData() {
        String h = "<html><body>pre <script>inner</script> aft</body></html>";
        Document doc = Jsoup.parse(h);
        assertEquals("<html><head></head><body>pre <script>inner</script> aft</body></html>", TextUtil.stripNewlines(doc.html()));
    }
}
