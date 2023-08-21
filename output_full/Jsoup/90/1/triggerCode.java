package org.jsoup.helper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.MultiLocaleRule;
import org.jsoup.MultiLocaleRule.MultiLocaleTest;
import org.jsoup.integration.ParseTest;
import org.junit.Rule;
import org.junit.Test;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class HttpConnectionTest {
    @Rule public MultiLocaleRule rule = new MultiLocaleRule();
    @Test public void handlesHeaderEncodingOnRequest() {
        Connection.Request req = new HttpConnection.Request();
        req.addHeader("xxx", "é");
    }
}
