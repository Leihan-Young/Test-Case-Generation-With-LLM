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
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import org.junit.Test;
/**
 * JUnit testcases for org.apache.commons.compress.archivers.zip.ZipEntry.
 *
 */
public class ZipArchiveEntryTest {
    /**
     * @see "https://issues.apache.org/jira/browse/COMPRESS-379"
     */
    @Test
    public void isUnixSymlinkIsFalseIfMoreThanOneFlagIsSet() throws Exception {
        try (ZipFile zf = new ZipFile(getFile("COMPRESS-379.jar"))) {
            ZipArchiveEntry ze = zf.getEntry("META-INF/maven/");
            assertFalse(ze.isUnixSymlink());
        }
    }
}
