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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import junit.framework.TestCase;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
/**
 * Test cases for Base64 class.
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
 * @author Apache Software Foundation
 * @version $Id$
 */
public class Base64Test extends TestCase {
    private Random _random = new Random();
    private void testEncodeOverMaxSize(int maxSize) throws Exception {
        try {
            Base64.encodeBase64(Base64TestData.DECODED, true, false, maxSize);
            fail("Expected " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException e) {
            // Expceted
        }
    }
    private void testDecodeEncode(String encodedText) {
        String decodedText = StringUtils.newStringUsAscii(Base64.decodeBase64(encodedText));
        String encodedText2 = Base64.encodeBase64String(StringUtils.getBytesUtf8(decodedText));
        assertEquals(encodedText, encodedText2);
    }
    private void testEncodeDecode(String plainText) {
        String encodedText = Base64.encodeBase64String(StringUtils.getBytesUtf8(plainText));
        String decodedText = StringUtils.newStringUsAscii(Base64.decodeBase64(encodedText));
        assertEquals(plainText, decodedText);
    }
    private String toString(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buf.append(data[i]);
            if (i != data.length - 1) {
                buf.append(",");
            }
        }
        return buf.toString();
    }
    public void testByteToStringVariations() throws DecoderException {
        Base64 base64 = new Base64(0);
        byte[] b1 = StringUtils.getBytesUtf8("Hello World");
        byte[] b2 = new byte[0];
        byte[] b3 = null;
        byte[] b4 = Hex.decodeHex("2bf7cc2701fe4397b49ebeed5acc7090".toCharArray());  // for url-safe tests

        assertEquals("byteToString Hello World", "SGVsbG8gV29ybGQ=", base64.encodeToString(b1));
        assertEquals("byteToString static Hello World", "SGVsbG8gV29ybGQ=", Base64.encodeBase64String(b1));
        assertEquals("byteToString \"\"", "", base64.encodeToString(b2));
        assertEquals("byteToString static \"\"", "", Base64.encodeBase64String(b2));
        assertEquals("byteToString null", null, base64.encodeToString(b3));
        assertEquals("byteToString static null", null, Base64.encodeBase64String(b3));
        assertEquals("byteToString UUID", "K/fMJwH+Q5e0nr7tWsxwkA==", base64.encodeToString(b4));
        assertEquals("byteToString static UUID", "K/fMJwH+Q5e0nr7tWsxwkA==", Base64.encodeBase64String(b4));
        assertEquals("byteToString static-url-safe UUID", "K_fMJwH-Q5e0nr7tWsxwkA", Base64.encodeBase64URLSafeString(b4));
    }
}
