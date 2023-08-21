package org.jsoup.integration;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.integration.servlets.Deflateservlet;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.FileServlet;
import org.jsoup.integration.servlets.HelloServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
import org.jsoup.integration.servlets.RedirectServlet;
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
import static org.jsoup.helper.HttpConnection.CONTENT_TYPE;
import static org.jsoup.helper.HttpConnection.MULTIPART_FORM_DATA;
import static org.jsoup.integration.UrlConnectTest.browserUa;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 * Tests Jsoup.connect against a local server.
 */
public class ConnectTest {
    private static String echoUrl;
    private static String ihVal(String key, Document doc) {
        final Element first = doc.select("th:contains(" + key + ") + td").first();
        return first != null ? first.text() : null;
    }
    @Test
    public void testBinaryThrowsExceptionWhenTypeIgnored() {
        Connection con = Jsoup.connect(FileServlet.Url);
        con.data(FileServlet.LocationParam, "/htmltests/thumb.jpg");
        con.data(FileServlet.ContentTypeParam, "image/jpeg");
        con.ignoreContentType(true);

        boolean threw = false;
        try {
            con.execute();
            Document doc = con.response().parse();
        } catch (IOException e) {
            threw = true;
            assertEquals("Input is binary and unsupported", e.getMessage());
        }
        assertTrue(threw);
    }
}
