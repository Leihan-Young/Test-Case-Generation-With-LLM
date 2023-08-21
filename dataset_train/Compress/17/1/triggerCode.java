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
package org.apache.commons.compress.archivers.tar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.compress.utils.CharsetNames;
import org.junit.Test;
public class TarArchiveInputStreamTest {
    private void datePriorToEpoch(String archive) throws Exception {
        URL tar = getClass().getResource(archive);
        TarArchiveInputStream in = null;
        try {
            in = new TarArchiveInputStream(new FileInputStream(new File(new URI(tar.toString()))));
            TarArchiveEntry tae = in.getNextTarEntry();
            assertEquals("foo", tae.getName());
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.set(1969, 11, 31, 23, 59, 59);
            cal.set(Calendar.MILLISECOND, 0);
            assertEquals(cal.getTime(), tae.getLastModifiedDate());
            assertTrue(tae.isCheckSumOK());
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    private TarArchiveInputStream getTestStream(String name) {
        return new TarArchiveInputStream(
                TarArchiveInputStreamTest.class.getResourceAsStream(name));
    }
    @Test
    public void testCompress197() throws Exception {
        TarArchiveInputStream tar = getTestStream("/COMPRESS-197.tar");
        try {
            TarArchiveEntry entry = tar.getNextTarEntry();
            while (entry != null) {
                entry = tar.getNextTarEntry();
            }
        } catch (IOException e) {
            fail("COMPRESS-197: " + e.getMessage());
        } finally {
            tar.close();
        }
    }
}
