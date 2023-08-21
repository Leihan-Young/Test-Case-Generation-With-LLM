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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.zip.ZipException;
import static org.apache.commons.compress.AbstractTestCase.getFile;
import static org.apache.commons.compress.AbstractTestCase.mkdir;
import static org.apache.commons.compress.AbstractTestCase.rmdir;
import static org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp.ACCESS_TIME_BIT;
import static org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp.CREATE_TIME_BIT;
import static org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp.MODIFY_TIME_BIT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class X5455_ExtendedTimestampTest {
    private final static ZipShort X5455 = new ZipShort(0x5455);
    private final static ZipLong ZERO_TIME = new ZipLong(0);
    private final static ZipLong MAX_TIME_SECONDS = new ZipLong(Integer.MAX_VALUE);
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss Z");
    private void parseReparse(
            final ZipLong time,
            final byte[] expectedLocal,
            final byte[] almostExpectedCentral
    ) throws ZipException {
        parseReparse(expectedLocal[0], time, expectedLocal[0], expectedLocal, almostExpectedCentral);
    }
    private void parseReparse(
            final byte providedFlags,
            final ZipLong time,
            final byte expectedFlags,
            final byte[] expectedLocal,
            final byte[] almostExpectedCentral
    ) throws ZipException {

        // We're responsible for expectedCentral's flags.  Too annoying to set in caller.
        final byte[] expectedCentral = new byte[almostExpectedCentral.length];
        System.arraycopy(almostExpectedCentral, 0, expectedCentral, 0, almostExpectedCentral.length);
        expectedCentral[0] = expectedFlags;

        xf.setModifyTime(time);
        xf.setAccessTime(time);
        xf.setCreateTime(time);
        xf.setFlags(providedFlags);
        byte[] result = xf.getLocalFileDataData();
        assertTrue(Arrays.equals(expectedLocal, result));

        // And now we re-parse:
        xf.parseFromLocalFileData(result, 0, result.length);
        assertEquals(expectedFlags, xf.getFlags());
        if (isFlagSet(expectedFlags, MODIFY_TIME_BIT)) {
            assertTrue(xf.isBit0_modifyTimePresent());
            assertEquals(time, xf.getModifyTime());
        }
        if (isFlagSet(expectedFlags, ACCESS_TIME_BIT)) {
            assertTrue(xf.isBit1_accessTimePresent());
            assertEquals(time, xf.getAccessTime());
        }
        if (isFlagSet(expectedFlags, CREATE_TIME_BIT)) {
            assertTrue(xf.isBit2_createTimePresent());
            assertEquals(time, xf.getCreateTime());
        }

        // Do the same as above, but with Central Directory data:
        xf.setModifyTime(time);
        xf.setAccessTime(time);
        xf.setCreateTime(time);
        xf.setFlags(providedFlags);
        result = xf.getCentralDirectoryData();
        assertTrue(Arrays.equals(expectedCentral, result));

        // And now we re-parse:
        xf.parseFromCentralDirectoryData(result, 0, result.length);
        assertEquals(expectedFlags, xf.getFlags());
        // Central Directory never contains ACCESS or CREATE, but
        // may contain MODIFY.
        if (isFlagSet(expectedFlags, MODIFY_TIME_BIT)) {
            assertTrue(xf.isBit0_modifyTimePresent());
            assertEquals(time, xf.getModifyTime());
        }
    }
    private static boolean isFlagSet(final byte data, final byte flag) { return (data & flag) == flag; }
    /**
     * InfoZIP seems to adjust the time stored inside the LFH and CD
     * to GMT when writing ZIPs while java.util.zip.ZipEntry thinks it
     * was in local time.
     *
     * The archive read in {@link #testSampleFile} has been created
     * with GMT-8 so we need to adjust for the difference.
     */
    private static Date adjustFromGMTToExpectedOffset(final Date from) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.MILLISECOND, cal.get(Calendar.ZONE_OFFSET));
        if (cal.getTimeZone().inDaylightTime(from)) {
            cal.add(Calendar.MILLISECOND, cal.get(Calendar.DST_OFFSET));
        }
        cal.add(Calendar.HOUR, 8);
        return cal.getTime();
    }
    @Test
    public void testGettersSetters() {
        // X5455 is concerned with time, so let's
        // get a timestamp to play with (Jan 1st, 2000).
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2000);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final long timeMillis = cal.getTimeInMillis();
        final ZipLong time = new ZipLong(timeMillis / 1000);

        // set too big
        try {
            // Java time is 1000 x larger (milliseconds).
            xf.setModifyJavaTime(new Date(1000L * (MAX_TIME_SECONDS.getValue() + 1L)));
            fail("Time too big for 32 bits!");
        } catch (final IllegalArgumentException iae) {
            // All is good.
        }

        // get/set modify time
        xf.setModifyTime(time);
        assertEquals(time, xf.getModifyTime());
        Date xfModifyJavaTime = xf.getModifyJavaTime();
        assertEquals(timeMillis, xfModifyJavaTime.getTime());
        xf.setModifyJavaTime(new Date(timeMillis));
        assertEquals(time, xf.getModifyTime());
        assertEquals(timeMillis, xf.getModifyJavaTime().getTime());
        // Make sure milliseconds get zeroed out:
        xf.setModifyJavaTime(new Date(timeMillis + 123));
        assertEquals(time, xf.getModifyTime());
        assertEquals(timeMillis, xf.getModifyJavaTime().getTime());
        // Null
        xf.setModifyTime(null);
        assertNull(xf.getModifyJavaTime());
        xf.setModifyJavaTime(null);
        assertNull(xf.getModifyTime());

        // get/set access time
        xf.setAccessTime(time);
        assertEquals(time, xf.getAccessTime());
        assertEquals(timeMillis, xf.getAccessJavaTime().getTime());
        xf.setAccessJavaTime(new Date(timeMillis));
        assertEquals(time, xf.getAccessTime());
        assertEquals(timeMillis, xf.getAccessJavaTime().getTime());
        // Make sure milliseconds get zeroed out:
        xf.setAccessJavaTime(new Date(timeMillis + 123));
        assertEquals(time, xf.getAccessTime());
        assertEquals(timeMillis, xf.getAccessJavaTime().getTime());
        // Null
        xf.setAccessTime(null);
        assertNull(xf.getAccessJavaTime());
        xf.setAccessJavaTime(null);
        assertNull(xf.getAccessTime());

        // get/set create time
        xf.setCreateTime(time);
        assertEquals(time, xf.getCreateTime());
        assertEquals(timeMillis, xf.getCreateJavaTime().getTime());
        xf.setCreateJavaTime(new Date(timeMillis));
        assertEquals(time, xf.getCreateTime());
        assertEquals(timeMillis, xf.getCreateJavaTime().getTime());
        // Make sure milliseconds get zeroed out:
        xf.setCreateJavaTime(new Date(timeMillis + 123));
        assertEquals(time, xf.getCreateTime());
        assertEquals(timeMillis, xf.getCreateJavaTime().getTime());
        // Null
        xf.setCreateTime(null);
        assertNull(xf.getCreateJavaTime());
        xf.setCreateJavaTime(null);
        assertNull(xf.getCreateTime());


        // initialize for flags
        xf.setModifyTime(time);
        xf.setAccessTime(time);
        xf.setCreateTime(time);

        // get/set flags: 000
        xf.setFlags((byte) 0);
        assertEquals(0, xf.getFlags());
        assertFalse(xf.isBit0_modifyTimePresent());
        assertFalse(xf.isBit1_accessTimePresent());
        assertFalse(xf.isBit2_createTimePresent());
        // Local length=1, Central length=1 (flags only!)
        assertEquals(1, xf.getLocalFileDataLength().getValue());
        assertEquals(1, xf.getCentralDirectoryLength().getValue());

        // get/set flags: 001
        xf.setFlags((byte) 1);
        assertEquals(1, xf.getFlags());
        assertTrue(xf.isBit0_modifyTimePresent());
        assertFalse(xf.isBit1_accessTimePresent());
        assertFalse(xf.isBit2_createTimePresent());
        // Local length=5, Central length=5 (flags + mod)
        assertEquals(5, xf.getLocalFileDataLength().getValue());
        assertEquals(5, xf.getCentralDirectoryLength().getValue());

        // get/set flags: 010
        xf.setFlags((byte) 2);
        assertEquals(2, xf.getFlags());
        assertFalse(xf.isBit0_modifyTimePresent());
        assertTrue(xf.isBit1_accessTimePresent());
        assertFalse(xf.isBit2_createTimePresent());
        // Local length=5, Central length=1
        assertEquals(5, xf.getLocalFileDataLength().getValue());
        assertEquals(1, xf.getCentralDirectoryLength().getValue());

        // get/set flags: 100
        xf.setFlags((byte) 4);
        assertEquals(4, xf.getFlags());
        assertFalse(xf.isBit0_modifyTimePresent());
        assertFalse(xf.isBit1_accessTimePresent());
        assertTrue(xf.isBit2_createTimePresent());
        // Local length=5, Central length=1
        assertEquals(5, xf.getLocalFileDataLength().getValue());
        assertEquals(1, xf.getCentralDirectoryLength().getValue());

        // get/set flags: 111
        xf.setFlags((byte) 7);
        assertEquals(7, xf.getFlags());
        assertTrue(xf.isBit0_modifyTimePresent());
        assertTrue(xf.isBit1_accessTimePresent());
        assertTrue(xf.isBit2_createTimePresent());
        // Local length=13, Central length=5
        assertEquals(13, xf.getLocalFileDataLength().getValue());
        assertEquals(5, xf.getCentralDirectoryLength().getValue());

        // get/set flags: 11111111
        xf.setFlags((byte) -1);
        assertEquals(-1, xf.getFlags());
        assertTrue(xf.isBit0_modifyTimePresent());
        assertTrue(xf.isBit1_accessTimePresent());
        assertTrue(xf.isBit2_createTimePresent());
        // Local length=13, Central length=5
        assertEquals(13, xf.getLocalFileDataLength().getValue());
        assertEquals(5, xf.getCentralDirectoryLength().getValue());
    }
}
