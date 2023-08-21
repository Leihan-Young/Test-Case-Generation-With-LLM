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
import static org.apache.commons.compress.AbstractTestCase.getFile;
import static org.apache.commons.compress.AbstractTestCase.mkdir;
import static org.apache.commons.compress.AbstractTestCase.rmdir;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;
public class TarArchiveInputStreamTest {
    private void datePriorToEpoch(String archive) throws Exception {
        TarArchiveInputStream in = null;
        try {
            in = new TarArchiveInputStream(new FileInputStream(getFile(archive)));
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
    public void shouldReadBigGid() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
        tos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        TarArchiveEntry t = new TarArchiveEntry("name");
        t.setGroupId(4294967294l);
        t.setSize(1);
        tos.putArchiveEntry(t);
        tos.write(30);
        tos.closeArchiveEntry();
        tos.close();
        byte[] data = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        TarArchiveInputStream tis =
            new TarArchiveInputStream(bis);
        t = tis.getNextTarEntry();
        assertEquals(4294967294l, t.getLongGroupId());
        tis.close();
    }
}
