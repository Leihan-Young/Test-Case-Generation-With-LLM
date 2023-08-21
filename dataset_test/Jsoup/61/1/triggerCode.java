package org.jsoup.select;
import org.jsoup.Jsoup;
import org.jsoup.TextUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 Tests for ElementList.

 @author Jonathan Hedley, jonathan@hedley.net */
public class ElementsTest {
    @Test public void hasClassCaseInsensitive() {
        Elements els = Jsoup.parse("<p Class=One>One <p class=Two>Two <p CLASS=THREE>THREE").select("p");
        Element one = els.get(0);
        Element two = els.get(1);
        Element thr = els.get(2);

        assertTrue(one.hasClass("One"));
        assertTrue(one.hasClass("ONE"));

        assertTrue(two.hasClass("TWO"));
        assertTrue(two.hasClass("Two"));

        assertTrue(thr.hasClass("ThreE"));
        assertTrue(thr.hasClass("three"));
    }
}
