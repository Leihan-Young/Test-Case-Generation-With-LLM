package org.jsoup.helper;
import org.jsoup.Jsoup;
import org.jsoup.integration.ParseTest;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.File;
import java.io.IOException;
import static org.jsoup.TextUtil.LE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class W3CDomTest {
    @Test
    public void handlesInvalidAttributeNames() {
        String html = "<html><head></head><body style=\"color: red\" \" name\"></body></html>";
        org.jsoup.nodes.Document jsoupDoc;
        jsoupDoc = Jsoup.parse(html);
        Element body = jsoupDoc.select("body").first();
        assertTrue(body.hasAttr("\"")); // actually an attribute with key '"'. Correct per HTML5 spec, but w3c xml dom doesn't dig it
        assertTrue(body.hasAttr("name\""));

        Document w3Doc = new W3CDom().fromJsoup(jsoupDoc);
    }
}
