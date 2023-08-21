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
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.compress.AbstractTestCase;
public class SevenZOutputFileTest extends AbstractTestCase {
    private File output;
    private void testCompress252(int numberOfFiles, int numberOfNonEmptyFiles)
        throws Exception {
        int nonEmptyModulus = numberOfNonEmptyFiles != 0
            ? numberOfFiles / numberOfNonEmptyFiles
            : numberOfFiles + 1;
        output = new File(dir, "COMPRESS252-" + numberOfFiles + "-" + numberOfNonEmptyFiles + ".7z");
        SevenZOutputFile archive = new SevenZOutputFile(output);
        try {
            addDir(archive);
            for (int i = 0; i < numberOfFiles; i++) {
                addFile(archive, i, (i + 1) % nonEmptyModulus == 0);
            }
        } finally {
            archive.close();
        }
        verifyCompress252(output, numberOfFiles, numberOfNonEmptyFiles);
    }
    private void verifyCompress252(File output, int numberOfFiles, int numberOfNonEmptyFiles)
        throws Exception {
        SevenZFile archive = new SevenZFile(output);
        int filesFound = 0;
        int nonEmptyFilesFound = 0;
        try {
            verifyDir(archive);
            Boolean b = verifyFile(archive, filesFound++);
            while (b != null) {
                if (Boolean.TRUE.equals(b)) {
                    nonEmptyFilesFound++;
                }
                b = verifyFile(archive, filesFound++);
            }
        } finally {
            archive.close();
        }
        assertEquals(numberOfFiles + 1, filesFound);
        assertEquals(numberOfNonEmptyFiles, nonEmptyFilesFound);
    }
    private void addDir(SevenZOutputFile archive) throws Exception {
        SevenZArchiveEntry entry = archive.createArchiveEntry(dir, "foo/");
        archive.putArchiveEntry(entry);
        archive.closeArchiveEntry();
    }
    private void verifyDir(SevenZFile archive) throws Exception {
        SevenZArchiveEntry entry = archive.getNextEntry();
        assertNotNull(entry);
        assertEquals("foo/", entry.getName());
        assertTrue(entry.isDirectory());
    }
    private void addFile(SevenZOutputFile archive, int index, boolean nonEmpty)
        throws Exception {
        SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName("foo/" + index + ".txt");
        archive.putArchiveEntry(entry);
        archive.write(nonEmpty ? new byte[] { 17 } : new byte[0]);
        archive.closeArchiveEntry();
    }
    private Boolean verifyFile(SevenZFile archive, int index) throws Exception {
        SevenZArchiveEntry entry = archive.getNextEntry();
        if (entry == null) {
            return null;
        }
        assertEquals("foo/" + index + ".txt", entry.getName());
        assertEquals(false, entry.isDirectory());
        if (entry.getSize() == 0) {
            return false;
        }
        assertEquals(1, entry.getSize());
        assertEquals(17, archive.read());
        assertEquals(-1, archive.read());
        return true;
    }
    public void testSevenEmptyFiles() throws Exception {
        testCompress252(7, 0);
    }
}
