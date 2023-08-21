/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.utils;
import static org.junit.Assert.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Test;
public class BitInputStreamTest {
    private ByteArrayInputStream getStream() {
        return new ByteArrayInputStream(new byte[] {
                (byte) 0xF8,  // 11111000
                0x40,         // 01000000
                0x01,         // 00000001
                0x2F });      // 00101111
    }
    /**
     * @see "https://issues.apache.org/jira/browse/COMPRESS-363"
     */
    @Test
    public void littleEndianWithOverflow() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] {
                87, // 01010111
                45, // 00101101
                66, // 01000010
                15, // 00001111
                90, // 01011010
                29, // 00011101
                88, // 01011000
                61, // 00111101
                33, // 00100001
                74  // 01001010
            });
        BitInputStream bin = new BitInputStream(in, ByteOrder.LITTLE_ENDIAN);
        assertEquals(23, // 10111 bin.readBits(5));
        assertEquals(714595605644185962l, // 0001-00111101-01011000-00011101-01011010-00001111-01000010-00101101-010 bin.readBits(63));
        assertEquals(1186, // 01001010-0010 bin.readBits(12));
        assertEquals(-1 , bin.readBits(1));
    }
}
