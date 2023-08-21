/*
 * Copyright 2002-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang.time;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.commons.lang.SystemUtils;
/**
 * Unit tests {@link org.apache.commons.lang.time.DateUtils}.
 *
 * @author <a href="mailto:sergek@lokitech.com">Serge Knystautas</a>
 * @author <a href="mailto:steve@mungoknotwise.com">Steven Caswell</a>
 */
public class DateUtilsTest extends TestCase {
    private static final long MILLIS_TEST;
    private void assertDate(Date date, int year, int month, int day, int hour, int min, int sec, int mil) throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        assertEquals(year, cal.get(Calendar.YEAR));
        assertEquals(month, cal.get(Calendar.MONTH));
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(min, cal.get(Calendar.MINUTE));
        assertEquals(sec, cal.get(Calendar.SECOND));
        assertEquals(mil, cal.get(Calendar.MILLISECOND));
    }
    /**
     * This checks that this is a 7 element iterator of Calendar objects
     * that are dates (no time), and exactly 1 day spaced after each other.
     */
    private static void assertWeekIterator(Iterator it, Calendar start) {
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DATE, 6);

        assertWeekIterator(it, start, end);
    }
    /**
     * Convenience method for when working with Date objects
     */
    private static void assertWeekIterator(Iterator it, Date start, Date end) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);

        assertWeekIterator(it, calStart, calEnd);
    }
    /**
     * This checks that this is a 7 divisble iterator of Calendar objects
     * that are dates (no time), and exactly 1 day spaced after each other
     * (in addition to the proper start and stop dates)
     */
    private static void assertWeekIterator(Iterator it, Calendar start, Calendar end) {
        Calendar cal = (Calendar) it.next();
        assertEquals("", start, cal, 0);
        Calendar last = null;
        int count = 1;
        while (it.hasNext()) {
            //Check this is just a date (no time component)
            assertEquals("", cal, DateUtils.truncate(cal, Calendar.DATE), 0);

            last = cal;
            cal = (Calendar) it.next();
            count++;

            //Check that this is one day more than the last date
            last.add(Calendar.DATE, 1);
            assertEquals("", last, cal, 0);
        }
        if (count % 7 != 0) {
            throw new AssertionFailedError("There were " + count + " days in this iterator");
        }
        assertEquals("", end, cal, 0);
    }
    /**
     * Used to check that Calendar objects are close enough
     * delta is in milliseconds
     */
    private static void assertEquals(String message, Calendar cal1, Calendar cal2, long delta) {
        if (Math.abs(cal1.getTime().getTime() - cal2.getTime().getTime()) > delta) {
            throw new AssertionFailedError(
                    message + " expected " + cal1.getTime() + " but got " + cal2.getTime());
        }
    }
    /**
     * Tests for LANG-59
     *
     * see http://issues.apache.org/jira/browse/LANG-59
     */
    public void testTruncateLang59() throws Exception {

        // Set TimeZone to Mountain Time
        TimeZone MST_MDT = TimeZone.getTimeZone("MST7MDT");
        TimeZone.setDefault(MST_MDT);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
        format.setTimeZone(MST_MDT);

        Date oct31_01MDT = new Date(1099206000000L); 

        Date oct31MDT             = new Date(oct31_01MDT.getTime()       - 3600000L); // - 1 hour
        Date oct31_01_02MDT       = new Date(oct31_01MDT.getTime()       + 120000L);  // + 2 minutes
        Date oct31_01_02_03MDT    = new Date(oct31_01_02MDT.getTime()    + 3000L);    // + 3 seconds
        Date oct31_01_02_03_04MDT = new Date(oct31_01_02_03MDT.getTime() + 4L);       // + 4 milliseconds

        assertEquals("Check 00:00:00.000", "2004-10-31 00:00:00.000 MDT", format.format(oct31MDT));
        assertEquals("Check 01:00:00.000", "2004-10-31 01:00:00.000 MDT", format.format(oct31_01MDT));
        assertEquals("Check 01:02:00.000", "2004-10-31 01:02:00.000 MDT", format.format(oct31_01_02MDT));
        assertEquals("Check 01:02:03.000", "2004-10-31 01:02:03.000 MDT", format.format(oct31_01_02_03MDT));
        assertEquals("Check 01:02:03.004", "2004-10-31 01:02:03.004 MDT", format.format(oct31_01_02_03_04MDT));

        // ------- Demonstrate Problem -------
        Calendar gval = Calendar.getInstance();
        gval.setTime(new Date(oct31_01MDT.getTime()));
        gval.set(Calendar.MINUTE, gval.get(Calendar.MINUTE)); // set minutes to the same value
        assertEquals("Demonstrate Problem", gval.getTime().getTime(), oct31_01MDT.getTime() + 3600000L);

        // ---------- Test Truncate ----------
        assertEquals("Truncate Calendar.MILLISECOND", oct31_01_02_03_04MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.MILLISECOND));

        assertEquals("Truncate Calendar.SECOND", oct31_01_02_03MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.SECOND));

        assertEquals("Truncate Calendar.MINUTE", oct31_01_02MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.MINUTE));

        assertEquals("Truncate Calendar.HOUR_OF_DAY", oct31_01MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.HOUR_OF_DAY));

        assertEquals("Truncate Calendar.HOUR", oct31_01MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.HOUR));

        assertEquals("Truncate Calendar.DATE", oct31MDT, DateUtils.truncate(oct31_01_02_03_04MDT, Calendar.DATE));


        // ---------- Test Round (down) ----------
        assertEquals("Round Calendar.MILLISECOND", oct31_01_02_03_04MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.MILLISECOND));

        assertEquals("Round Calendar.SECOND", oct31_01_02_03MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.SECOND));

        assertEquals("Round Calendar.MINUTE", oct31_01_02MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.MINUTE));

        assertEquals("Round Calendar.HOUR_OF_DAY", oct31_01MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.HOUR_OF_DAY));

        assertEquals("Round Calendar.HOUR", oct31_01MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.HOUR));

        assertEquals("Round Calendar.DATE", oct31MDT, DateUtils.round(oct31_01_02_03_04MDT, Calendar.DATE));

        // restore default time zone
        TimeZone.setDefault(defaultZone);
    }
}
