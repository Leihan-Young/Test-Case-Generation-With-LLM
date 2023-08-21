package org.jsoup.nodes;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests for the DocumentType node
 *
 * @author Jonathan Hedley, http://jonathanhedley.com/
 */
public class DocumentTypeTest {
    @Test
    public void constructorValidationOkWithBlankName() {
        DocumentType fail = new DocumentType("","", "", "");
    }
}
