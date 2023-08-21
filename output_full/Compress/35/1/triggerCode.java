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
package org.apache.commons.compress;
import static org.junit.Assert.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.Test;
public final class DetectArchiverTestCase extends AbstractTestCase {
    private ArchiveInputStream getStreamFor(String resource)
            throws ArchiveException, IOException {
        return factory.createArchiveInputStream(
                   new BufferedInputStream(new FileInputStream(
                       getFile(resource))));
    }
    private void checkEmptyArchive(String type) throws Exception{
        File ar = createEmptyArchive(type); // will be deleted by tearDown()
        ar.deleteOnExit(); // Just in case file cannot be deleted
        ArchiveInputStream ais = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(ar));
            ais = factory.createArchiveInputStream(in);
        } catch (ArchiveException ae) {
            fail("Should have recognised empty archive for "+type);
        } finally {
            if (ais != null) {
                ais.close(); // will close input as well
            } else if (in != null){
                in.close();
            }
        }
    }
    @Test
    public void testCOMPRESS335() throws Exception {
        final ArchiveInputStream tar = getStreamFor("COMPRESS-335.tar");
        assertNotNull(tar);
        assertTrue(tar instanceof TarArchiveInputStream);
    }
}
