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
import static org.junit.Assert.assertTrue;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.commons.compress.utils.IOUtils;
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
    /**
     * Test case for 
     * <a href="https://issues.apache.org/jira/browse/COMPRESS-264"
     * >COMPRESS-264</a>.
     */
    @Test
    public void testReadingOfFirstStoredEntry() throws Exception {
        ZipArchiveInputStream in = new ZipArchiveInputStream(new FileInputStream(getFile("COMPRESS-264.zip")));
        
        try {
            ZipArchiveEntry ze = in.getNextZipEntry();
            assertEquals(5, ze.getSize());
            assertArrayEquals(new byte[] {'d', 'a', 't', 'a', '\n'}, IOUtils.toByteArray(in));
        } finally {
            in.close();
        }
    }
}
