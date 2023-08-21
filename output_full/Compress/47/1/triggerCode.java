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
package org.apache.commons.compress.archivers.zip;
import static org.apache.commons.compress.AbstractTestCase.getFile;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Assert;
import org.junit.Test;
public class ZipArchiveInputStreamTest {
    private void extractZipInputStream(final ZipArchiveInputStream in)
        throws IOException {
        ZipArchiveEntry zae = in.getNextZipEntry();
        while (zae != null) {
            if (zae.getName().endsWith(".zip")) {
                extractZipInputStream(new ZipArchiveInputStream(in));
            }
            zae = in.getNextZipEntry();
        }
    }
    private static byte[] readEntry(ZipArchiveInputStream zip, ZipArchiveEntry zae) throws IOException {
        final int len = (int)zae.getSize();
        final byte[] buff = new byte[len];
        zip.read(buff, 0, len);

        return buff;
    }
    private static void nameSource(String archive, String entry, ZipArchiveEntry.NameSource expected) throws Exception {
        nameSource(archive, entry, 1, expected);
    }
    private static void nameSource(String archive, String entry, int entryNo, ZipArchiveEntry.NameSource expected)
        throws Exception {
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(getFile(archive)))) {
            ZipArchiveEntry ze;
            do {
                ze = zis.getNextZipEntry();
            } while (--entryNo > 0);
            assertEquals(entry, ze.getName());
            assertEquals(expected, ze.getNameSource());
        }
    }
    @Test
    public void properlyMarksEntriesAsUnreadableIfUncompressedSizeIsUnknown() throws Exception {
        // we never read any data
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(new byte[0]))) {
            ZipArchiveEntry e = new ZipArchiveEntry("test");
            e.setMethod(ZipMethod.DEFLATED.getCode());
            assertTrue(zis.canReadEntryData(e));
            e.setMethod(ZipMethod.ENHANCED_DEFLATED.getCode());
            assertTrue(zis.canReadEntryData(e));
            e.setMethod(ZipMethod.BZIP2.getCode());
            assertFalse(zis.canReadEntryData(e));
        }
    }
}
