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
package org.apache.commons.codec.binary;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Test;
public class Base32InputStreamTest {
    private static final String ENCODED_FOO = "MZXW6===";
    private static final String STRING_FIXTURE = "Hello World";
    private void testBase32EmptyInputStream(int chuckSize) throws Exception {
        byte[] emptyEncoded = new byte[0];
        byte[] emptyDecoded = new byte[0];
        testByteByByte(emptyEncoded, emptyDecoded, chuckSize, CRLF);
        testByChunk(emptyEncoded, emptyDecoded, chuckSize, CRLF);
    }
    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32InputStream wraps itself in encode and decode mode over and over
     * again.
     * 
     * @param encoded
     *            base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base32 encoded data.
     * @param seperator
     *            Line separator in the base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByChunk(byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator) throws Exception {

        // Start with encode.
        InputStream in = new ByteArrayInputStream(decoded);
        in = new Base32InputStream(in, true, chunkSize, seperator);
        byte[] output = Base32TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 encode", Arrays.equals(output, encoded));

        // Now let's try decode.
        in = new ByteArrayInputStream(encoded);
        in = new Base32InputStream(in);
        output = Base32TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 decode", Arrays.equals(output, decoded));

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base32InputStream(in, true, chunkSize, seperator);
            in = new Base32InputStream(in, false);
        }
        output = Base32TestData.streamToBytes(in);

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 wrap-wrap-wrap!", Arrays.equals(output, decoded));
    }
    /**
     * Tests method does three tests on the supplied data: 1. encoded ---[DECODE]--> decoded 2. decoded ---[ENCODE]--> encoded 3. decoded
     * ---[WRAP-WRAP-WRAP-etc...] --> decoded
     * <p/>
     * By "[WRAP-WRAP-WRAP-etc...]" we mean situation where the Base32InputStream wraps itself in encode and decode mode over and over
     * again.
     * 
     * @param encoded
     *            base32 encoded data
     * @param decoded
     *            the data from above, but decoded
     * @param chunkSize
     *            chunk size (line-length) of the base32 encoded data.
     * @param seperator
     *            Line separator in the base32 encoded data.
     * @throws Exception
     *             Usually signifies a bug in the Base32 commons-codec implementation.
     */
    private void testByteByByte(byte[] encoded, byte[] decoded, int chunkSize, byte[] seperator) throws Exception {

        // Start with encode.
        InputStream in = new ByteArrayInputStream(decoded);
        in = new Base32InputStream(in, true, chunkSize, seperator);
        byte[] output = new byte[encoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 encode", Arrays.equals(output, encoded));

        // Now let's try decode.
        in = new ByteArrayInputStream(encoded);
        in = new Base32InputStream(in);
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 decode", Arrays.equals(output, decoded));

        // I always wanted to do this! (wrap encoder with decoder etc etc).
        in = new ByteArrayInputStream(decoded);
        for (int i = 0; i < 10; i++) {
            in = new Base32InputStream(in, true, chunkSize, seperator);
            in = new Base32InputStream(in, false);
        }
        output = new byte[decoded.length];
        for (int i = 0; i < output.length; i++) {
            output[i] = (byte) in.read();
        }

        assertEquals("EOF", -1, in.read());
        assertEquals("Still EOF", -1, in.read());
        assertTrue("Streaming base32 wrap-wrap-wrap!", Arrays.equals(output, decoded));
    }
    /**
     * Tests skipping to the end of a stream.
     * 
     * @throws Throwable
     */
    @Test
    public void testSkipToEnd() throws Throwable {
        InputStream ins = new ByteArrayInputStream(StringUtils.getBytesIso8859_1(ENCODED_FOO));
        Base32InputStream b32stream = new Base32InputStream(ins);
        // due to CODEC-130, skip now skips correctly decoded characters rather than encoded
        assertEquals(3, b32stream.skip(3));
        // End of stream reached
        assertEquals(-1, b32stream.read());
        assertEquals(-1, b32stream.read());
    }
}
