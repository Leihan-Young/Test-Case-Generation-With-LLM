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
package org.apache.commons.compress.archivers;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream; 	
import java.io.FileOutputStream; 	
import java.io.IOException; 	
import java.io.InputStream; 	
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryPredicate;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream; 	
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream; 	
import org.apache.commons.compress.archivers.zip.ZipFile; 	
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.junit.Assert;
import org.junit.Test;
public final class ZipTestCase extends AbstractTestCase {
    private File createReferenceFile(final File directory, final Zip64Mode zipMode, final String prefix) throws IOException {
        final File reference = File.createTempFile(prefix, ".zip", directory);
        final ZipArchiveOutputStream zos = new ZipArchiveOutputStream(reference);
        zos.setUseZip64(zipMode);
        createFirstEntry(zos);
        createSecondEntry(zos);
        zos.close();
        return reference;
    }
    private ZipArchiveOutputStream createFirstEntry(final ZipArchiveOutputStream zos) throws IOException {
        createArchiveEntry(first_payload, zos, "file1.txt");
        return zos;
    }
    private ZipArchiveOutputStream createSecondEntry(final ZipArchiveOutputStream zos) throws IOException {
        createArchiveEntry(second_payload, zos, "file2.txt");
        return zos;
    }
    private void assertSameFileContents(final File expectedFile, final File actualFile) throws IOException {
        final int size = (int) Math.max(expectedFile.length(), actualFile.length());
        final ZipFile expected = new ZipFile(expectedFile);
        final ZipFile actual = new ZipFile(actualFile);
        final byte[] expectedBuf = new byte[size];
        final byte[] actualBuf = new byte[size];

        final Enumeration<ZipArchiveEntry> actualInOrder = actual.getEntriesInPhysicalOrder();
        final Enumeration<ZipArchiveEntry> expectedInOrder = expected.getEntriesInPhysicalOrder();

        while (actualInOrder.hasMoreElements()){
            final ZipArchiveEntry actualElement = actualInOrder.nextElement();
            final ZipArchiveEntry expectedElement = expectedInOrder.nextElement();
            assertEquals( expectedElement.getName(), actualElement.getName());
            // Don't compare timestamps since they may vary;
            // there's no support for stubbed out clock (TimeSource) in ZipArchiveOutputStream
            assertEquals( expectedElement.getMethod(), actualElement.getMethod());
            assertEquals( expectedElement.getGeneralPurposeBit(), actualElement.getGeneralPurposeBit());
            assertEquals( expectedElement.getCrc(), actualElement.getCrc());
            assertEquals( expectedElement.getCompressedSize(), actualElement.getCompressedSize());
            assertEquals( expectedElement.getSize(), actualElement.getSize());
            assertEquals( expectedElement.getExternalAttributes(), actualElement.getExternalAttributes());
            assertEquals( expectedElement.getInternalAttributes(), actualElement.getInternalAttributes());

            final InputStream actualIs = actual.getInputStream(actualElement);
            final InputStream expectedIs = expected.getInputStream(expectedElement);
            IOUtils.readFully(expectedIs, expectedBuf);
            IOUtils.readFully(actualIs, actualBuf);
            expectedIs.close();
            actualIs.close();
            Assert.assertArrayEquals(expectedBuf, actualBuf); // Buffers are larger than payload. dont care
        }

        expected.close();
        actual.close();
    }
    private void createArchiveEntry(final String payload, final ZipArchiveOutputStream zos, final String name)
            throws IOException {
        final ZipArchiveEntry in = new ZipArchiveEntry(name);
        zos.putArchiveEntry(in);

        zos.write(payload.getBytes());
        zos.closeArchiveEntry();
    }
    /**
     * Checks if all entries from a nested archive can be read.
     * The archive: OSX_ArchiveWithNestedArchive.zip contains:
     * NestedArchiv.zip and test.xml3.
     * 
     * The nested archive:  NestedArchive.zip contains test1.xml and test2.xml
     * 
     * @throws Exception
     */
    @Test
    public void testListAllFilesWithNestedArchive() throws Exception {
        final File input = getFile("OSX_ArchiveWithNestedArchive.zip");

        final List<String> results = new ArrayList<>();
        final List<ZipException> expectedExceptions = new ArrayList<>();

        final InputStream is = new FileInputStream(input);
        ArchiveInputStream in = null;
        try {
            in = new ArchiveStreamFactory().createArchiveInputStream("zip", is);

            ZipArchiveEntry entry = null;
            while ((entry = (ZipArchiveEntry) in.getNextEntry()) != null) {
                results.add(entry.getName());

                final ArchiveInputStream nestedIn = new ArchiveStreamFactory().createArchiveInputStream("zip", in);
                try {
                    ZipArchiveEntry nestedEntry = null;
                    while ((nestedEntry = (ZipArchiveEntry) nestedIn.getNextEntry()) != null) {
                        results.add(nestedEntry.getName());
                    }
                } catch (ZipException ex) {
                    // expected since you cannot create a final ArchiveInputStream from test3.xml
                    expectedExceptions.add(ex);
                }
                // nested stream must not be closed here
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        is.close();

        assertTrue(results.contains("NestedArchiv.zip"));
        assertTrue(results.contains("test1.xml"));
        assertTrue(results.contains("test2.xml"));
        assertTrue(results.contains("test3.xml"));
        assertEquals(1, expectedExceptions.size());
    }
}
