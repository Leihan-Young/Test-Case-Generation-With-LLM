/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.commons.compress.archivers.sevenz;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.PasswordRequiredException;
import org.junit.Test;
public class SevenZFileTest extends AbstractTestCase {
    private byte[] readFully(final SevenZFile archive) throws IOException {
        final byte [] buf = new byte [1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int len = 0; (len = archive.read(buf)) > 0;) {
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }
    private void test7zUnarchive(final File f, final SevenZMethod m) throws Exception {
        test7zUnarchive(f, m, null);
    }
    private void test7zUnarchive(final File f, final SevenZMethod m, final byte[] password) throws Exception {
        final SevenZFile sevenZFile = new SevenZFile(f, password);
        try {
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            assertEquals("test1.xml", entry.getName());
            assertEquals(m, entry.getContentMethods().iterator().next().getMethod());
            entry = sevenZFile.getNextEntry();
            assertEquals("test2.xml", entry.getName());
            assertEquals(m, entry.getContentMethods().iterator().next().getMethod());
            final byte[] contents = new byte[(int)entry.getSize()];
            int off = 0;
            while ((off < contents.length)) {
                final int bytesRead = sevenZFile.read(contents, off, contents.length - off);
                assert(bytesRead >= 0);
                off += bytesRead;
            }
            assertEquals(TEST2_CONTENT, new String(contents, "UTF-8"));
            assertNull(sevenZFile.getNextEntry());
        } finally {
            sevenZFile.close();
        }
    }
    private void checkHelloWorld(final String filename) throws Exception {
        final SevenZFile sevenZFile = new SevenZFile(getFile(filename));
        try {
            final SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            assertEquals("Hello world.txt", entry.getName());
            final byte[] contents = new byte[(int)entry.getSize()];
            int off = 0;
            while ((off < contents.length)) {
                final int bytesRead = sevenZFile.read(contents, off, contents.length - off);
                assert(bytesRead >= 0);
                off += bytesRead;
            }
            assertEquals("Hello, world!\n", new String(contents, "UTF-8"));
            assertNull(sevenZFile.getNextEntry());
        } finally {
            sevenZFile.close();
        }
    }
    private static boolean isStrongCryptoAvailable() throws NoSuchAlgorithmException {
        return Cipher.getMaxAllowedKeyLength("AES/ECB/PKCS5Padding") >= 256;
    }
    /**
     * @see "https://issues.apache.org/jira/browse/COMPRESS-348"
     */
    @Test
    public void readEntriesOfSize0() throws IOException {
        final SevenZFile sevenZFile = new SevenZFile(getFile("COMPRESS-348.7z"));
        try {
            int entries = 0;
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                entries++;
                int b = sevenZFile.read();
                if ("2.txt".equals(entry.getName()) || "5.txt".equals(entry.getName())) {
                    assertEquals(-1, b);
                } else {
                    assertNotEquals(-1, b);
                }
                entry = sevenZFile.getNextEntry();
            }
            assertEquals(5, entries);
        } finally {
            sevenZFile.close();
        }
    }
}
