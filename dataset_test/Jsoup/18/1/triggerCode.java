package org.jsoup.parser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class CharacterReaderTest {
    @Test public void handleCarriageReturnAsLineFeed() {
        String in = "one \r two \r\n three";
        CharacterReader r = new CharacterReader(in);

        String first = r.consumeTo('\n');
        assertEquals("one ", first);
        assertEquals("\n two \n three", r.consumeToEnd());
    }
}
