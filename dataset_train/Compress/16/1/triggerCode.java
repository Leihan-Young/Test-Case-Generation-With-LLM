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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Test;
public class ArchiveStreamFactoryTest {
    /**
     * see https://issues.apache.org/jira/browse/COMPRESS-191
     */
    @Test
    public void aiffFilesAreNoTARs() throws Exception {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream("src/test/resources/testAIFF.aif"));
            new ArchiveStreamFactory().createArchiveInputStream(is);
            fail("created an input stream for a non-archive");
        } catch (ArchiveException ae) {
            assertTrue(ae.getMessage().startsWith("No Archiver found"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
