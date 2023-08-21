/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.apache.commons.compress;
import static org.junit.Assert.*;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.junit.Test;
public class ArchiveUtilsTest extends AbstractTestCase {
    private static final int bytesToTest = 50;
    private static final byte[] byteTest = new byte[bytesToTest];
    private void asciiToByteAndBackOK(final String inputString) {
        assertEquals(inputString, ArchiveUtils.toAsciiString(ArchiveUtils.toAsciiBytes(inputString)));
    }
    private void asciiToByteAndBackFail(final String inputString) {
        assertFalse(inputString.equals(ArchiveUtils.toAsciiString(ArchiveUtils.toAsciiBytes(inputString))));
    }
    @Test
    public void sanitizeShortensString() {
        String input = "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789";
        String expected = "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901234567890123456789012345678901234567890123456789"
            + "012345678901...";
        assertEquals(expected, ArchiveUtils.sanitize(input));
    }
}
