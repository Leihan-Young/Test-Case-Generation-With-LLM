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
package org.apache.commons.compress.archivers.tar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.CharsetNames;
import org.junit.Test;
public class TarUtilsTest {
    private void checkRoundTripOctal(final long value, final int bufsize) {
        final byte [] buffer = new byte[bufsize];
        long parseValue;
        TarUtils.formatLongOctalBytes(value, buffer, 0, buffer.length);
        parseValue = TarUtils.parseOctal(buffer,0, buffer.length);
        assertEquals(value,parseValue);
    }
    private void checkRoundTripOctal(final long value) {
        checkRoundTripOctal(value, TarConstants.SIZELEN);
    }
    private void checkRoundTripOctalOrBinary(final long value, final int bufsize) {
        final byte [] buffer = new byte[bufsize];
        long parseValue;
        TarUtils.formatLongOctalOrBinaryBytes(value, buffer, 0, buffer.length);
        parseValue = TarUtils.parseOctalOrBinary(buffer,0, buffer.length);
        assertEquals(value,parseValue);
    }
    private void testRoundTripOctalOrBinary(final int length) {
        checkRoundTripOctalOrBinary(0, length);
        checkRoundTripOctalOrBinary(1, length);
        checkRoundTripOctalOrBinary(TarConstants.MAXSIZE, length); // will need binary format
        checkRoundTripOctalOrBinary(-1, length); // will need binary format
        checkRoundTripOctalOrBinary(0xff00000000000001l, length);
    }
    private void checkName(final String string) {
        final byte buff[] = new byte[100];
        final int len = TarUtils.formatNameBytes(string, buff, 0, buff.length);
        assertEquals(string, TarUtils.parseName(buff, 0, len));
    }
    @Test
    public void testRoundTripOctalOrBinary8() {
        testRoundTripOctalOrBinary(8);
    }
}
