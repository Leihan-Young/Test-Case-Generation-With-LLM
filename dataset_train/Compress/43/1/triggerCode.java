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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.commons.compress.AbstractTestCase.mkdir;
import static org.apache.commons.compress.AbstractTestCase.rmdir;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
public class DataDescriptorTest {
    private File dir;
    private int findCentralDirectory(byte[] data) {
        // not a ZIP64 archive, no comment, "End of central directory record" at the end
        return (int) ZipLong.getValue(data, data.length - 22 + 16);
    }
    @Test
    public void doesntWriteDataDescriptorWhenAddingRawEntries() throws IOException {
        ByteArrayOutputStream init = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(init)) {
            zos.putArchiveEntry(new ZipArchiveEntry("test1.txt"));
            zos.write("foo".getBytes("UTF-8"));
            zos.closeArchiveEntry();
        }

        File f = new File(dir, "test.zip");
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(init.toByteArray());
        }

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        ZipArchiveEntry zae;
        try (ZipFile zf = new ZipFile(f);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(o)) {
            zae = zf.getEntry("test1.txt");
            zos.addRawArchiveEntry(zae, zf.getRawInputStream(zae));
        }

        byte[] data = o.toByteArray();
        byte[] versionInLFH = Arrays.copyOfRange(data, 4, 6);
        // still 2.0 because of Deflate
        assertArrayEquals(new byte[] { 20, 0 }, versionInLFH);
        byte[] gpbInLFH = Arrays.copyOfRange(data, 6, 8);
        // no DD but EFS flag
        assertArrayEquals(new byte[] { 0, 8 }, gpbInLFH);

        int cdhStart = findCentralDirectory(data);
        byte[] versionInCDH = Arrays.copyOfRange(data, cdhStart + 6, cdhStart + 8);
        assertArrayEquals(new byte[] { 20, 0 }, versionInCDH);
        byte[] gpbInCDH = Arrays.copyOfRange(data, cdhStart + 8, cdhStart + 10);
        assertArrayEquals(new byte[] { 0, 8 }, gpbInCDH);

        int ddStart = cdhStart - 16;
        assertNotEquals(ZipLong.DD_SIG, new ZipLong(data, ddStart));
        long crcFromLFH = ZipLong.getValue(data, 14);
        long cSizeFromLFH = ZipLong.getValue(data, 18);
        long sizeFromLFH = ZipLong.getValue(data, 22);
        assertEquals(3, sizeFromLFH);

        long crcFromCDH = ZipLong.getValue(data, cdhStart + 16);
        assertEquals(crcFromLFH, crcFromCDH);
        long cSizeFromCDH = ZipLong.getValue(data, cdhStart + 20);
        assertEquals(cSizeFromLFH, cSizeFromCDH);
        long sizeFromCDH = ZipLong.getValue(data, cdhStart + 24);
        assertEquals(sizeFromLFH, sizeFromCDH);
    }
}
