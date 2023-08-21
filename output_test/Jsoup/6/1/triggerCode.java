package org.jsoup.integration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static org.junit.Assert.*;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
/**
 * Integration test: parses from real-world example HTML.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class ParseTest {
    @Test
    public void testYahooArticle() throws IOException {
        File in = getFile("/htmltests/yahoo-article-1.html");
        Document doc = Jsoup.parse(in, "UTF-8", "http://news.yahoo.com/s/nm/20100831/bs_nm/us_gm_china");
        Element p = doc.select("p:contains(Volt will be sold in the United States").first();
        assertEquals("In July, GM said its electric Chevrolet Volt will be sold in the United States at $41,000 -- $8,000 more than its nearest competitor, the Nissan Leaf.", p.text());
    }
}
