package org.jsoup.integration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.integration.servlets.Deflateservlet;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.HelloServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
import org.jsoup.integration.servlets.SlowRider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import static org.jsoup.integration.UrlConnectTest.browserUa;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * Tests Jsoup.connect against a local server.
 */
public class ConnectTest {
    private static String echoUrl;
    private static String ihVal(String key, Document doc) {
        return doc.select("th:contains(" + key + ") + td").first().text();
    }
    @Test
    public void handlesEmptyStreamDuringParseRead() throws IOException {
        // this handles situations where the remote server sets a content length greater than it actually writes

        Connection.Response res = Jsoup.connect(InterruptedServlet.Url)
            .timeout(200)
            .execute();

        boolean threw = false;
        try {
            Document document = res.parse();
            assertEquals("Something", document.title());
        } catch (IOException e) {
            threw = true;
        }
        assertEquals(true, threw);
    }
}
