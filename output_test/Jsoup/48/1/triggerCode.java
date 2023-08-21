package org.jsoup.helper;
import static org.junit.Assert.*;
import org.jsoup.integration.ParseTest;
import org.junit.Test;
import org.jsoup.Connection;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
public class HttpConnectionTest {
    @Test public void sameHeadersCombineWithComma() {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        List<String> values = new ArrayList<String>();
        values.add("no-cache");
        values.add("no-store");
        headers.put("Cache-Control", values);
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        assertEquals("no-cache, no-store", res.header("Cache-Control"));
    }
}
