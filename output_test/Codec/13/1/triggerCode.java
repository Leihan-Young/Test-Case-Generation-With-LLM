/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.codec.language;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Test;
/**
 * Tests {@link DoubleMetaphone}.
 *
 * <p>Keep this file in UTF-8 encoding for proper Javadoc processing.</p>
 *
 * @see "http://www.cuj.com/documents/s=8038/cuj0006philips/"
 * @version $Id$
 */
public class DoubleMetaphoneTest extends StringEncoderAbstractTest<DoubleMetaphone> {
    /**
     * Tests encoding APIs in one place.
     */
    private void assertDoubleMetaphone(final String expected, final String source) {
        assertEquals(expected, this.getStringEncoder().encode(source));
        try {
            assertEquals(expected, this.getStringEncoder().encode((Object) source));
        } catch (final EncoderException e) {
            fail("Unexpected expection: " + e);
        }
        assertEquals(expected, this.getStringEncoder().doubleMetaphone(source));
        assertEquals(expected, this.getStringEncoder().doubleMetaphone(source, false));
    }
    @Test
    public void testIsDoubleMetaphoneEqualBasic() {
        final String[][] testFixture = new String[][] { { 
                "", "" }, {
                "Case", "case" }, {
                "CASE", "Case" }, {
                "caSe", "cAsE" }, {
                "cookie", "quick" }, {
                "quick", "cookie" }, {
                "Brian", "Bryan" }, {
                "Auto", "Otto" }, {
                "Steven", "Stefan" }, {
                "Philipowitz", "Filipowicz" }
        };
        doubleMetaphoneEqualTest(testFixture, false);
        doubleMetaphoneEqualTest(testFixture, true);
    }
}
