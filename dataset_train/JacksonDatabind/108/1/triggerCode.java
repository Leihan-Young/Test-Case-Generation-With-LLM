package com.fasterxml.jackson.databind.node;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
/**
 * Tests to verify handling of empty content with "readTree()"
 */
public class EmptyContentAsTreeTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();
    private final String EMPTY0 = "";
    private final byte[] EMPTY0_BYTES = EMPTY0.getBytes(StandardCharsets.UTF_8);
    private final String EMPTY1 = "  \n\t  ";
    private final byte[] EMPTY1_BYTES = EMPTY1.getBytes(StandardCharsets.UTF_8);
    private void _assertNullTree(TreeNode n) {
        if (n != null) {
            fail("Should get `null` for reads with `JsonParser`, instead got: "+n.getClass().getName());
        }
    }
    private void _assertMissing(JsonNode n) {
        assertNotNull("Should not get `null` but `MissingNode`", n);
        if (!n.isMissingNode()) {
            fail("Should get `MissingNode` but got: "+n.getClass().getName());
        }
    }
    public void testNullFromEOFWithParserAndReader() throws Exception
    {
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY0)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY1)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(new StringReader(EMPTY0))) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(new StringReader(EMPTY1))) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }

        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY0_BYTES)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY1_BYTES)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(EMPTY1_BYTES, 0, EMPTY1_BYTES.length)) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }

        try (JsonParser p = MAPPER.getFactory().createParser(new ByteArrayInputStream(EMPTY0_BYTES))) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
        try (JsonParser p = MAPPER.getFactory().createParser(new ByteArrayInputStream(EMPTY1_BYTES))) {
            _assertNullTree(MAPPER.reader().readTree(p));
        }
    }
}
