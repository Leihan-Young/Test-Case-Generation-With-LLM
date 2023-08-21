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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import junit.framework.TestCase;
public class ZipFileTest extends TestCase {
    private ZipFile zf = null;
    /*
     * ordertest.zip has been handcrafted.
     *
     * It contains enough files so any random coincidence of
     * entries.keySet() and central directory order would be unlikely
     * - in fact testCDOrder fails in svn revision 920284.
     *
     * The central directory has ZipFile and ZipUtil swapped so
     * central directory order is different from entry data order.
     */
    private void readOrderTest() throws Exception {
        File archive = getFile("ordertest.zip");
        zf = new ZipFile(archive);
    }
    private static void assertEntryName(ArrayList<ZipArchiveEntry> entries,
                                        int index,
                                        String expectedName) {
        ZipArchiveEntry ze = entries.get(index);
        assertEquals("src/main/java/org/apache/commons/compress/archivers/zip/"
                     + expectedName + ".java",
                     ze.getName());
    }
    /**
     * @see https://issues.apache.org/jira/browse/COMPRESS-228
     */
    public void testExcessDataInZip64ExtraField() throws Exception {
        File archive = getFile("COMPRESS-228.zip");
        zf = new ZipFile(archive);
        // actually, if we get here, the test already has passed

        ZipArchiveEntry ze = zf.getEntry("src/main/java/org/apache/commons/compress/archivers/zip/ZipFile.java");
        assertEquals(26101, ze.getSize());
    }
}
