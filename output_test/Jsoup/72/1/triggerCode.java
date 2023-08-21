package org.jsoup.parser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Test suite for character reader.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class CharacterReaderTest {
    @Test
    public void consumeToNonexistentEndWhenAtAnd() {
        CharacterReader r = new CharacterReader("<!");
        assertTrue(r.matchConsume("<!"));
        assertTrue(r.isEmpty());

        String after = r.consumeTo('>');
        assertEquals("", after);

        assertTrue(r.isEmpty());
    }
}
