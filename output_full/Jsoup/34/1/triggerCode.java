package org.jsoup.parser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class CharacterReaderTest {
    @Test public void nextIndexOfUnmatched() {
        CharacterReader r = new CharacterReader("<[[one]]");
        assertEquals(-1, r.nextIndexOf("]]>"));
    }
}
