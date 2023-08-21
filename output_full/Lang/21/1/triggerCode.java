/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;
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
import junit.framework.TestCase;
import static org.apache.commons.lang3.JavaVersion.*;
import org.apache.commons.lang3.SystemUtils;
/**
 * Unit tests {@link org.apache.commons.lang3.time.DateUtils}.
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
    private static void assertWeekIterator(Iterator<?> it, Calendar start) {
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DATE, 6);

        assertWeekIterator(it, start, end);
    }
    /**
     * Convenience method for when working with Date objects
     */
    private static void assertWeekIterator(Iterator<?> it, Date start, Date end) {
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
    private static void assertWeekIterator(Iterator<?> it, Calendar start, Calendar end) {
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
    public void testIsSameLocalTime_Cal() {
        GregorianCalendar cal1 = new GregorianCalendar(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("GMT-1"));
        cal1.set(2004, 6, 9, 13, 45, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(2004, 6, 9, 13, 45, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        assertEquals(true, DateUtils.isSameLocalTime(cal1, cal2));

        Calendar cal3 = Calendar.getInstance();
        Calendar cal4 = Calendar.getInstance();
        cal3.set(2004, 6, 9, 4,  0, 0);
        cal4.set(2004, 6, 9, 16, 0, 0);
        cal3.set(Calendar.MILLISECOND, 0);
        cal4.set(Calendar.MILLISECOND, 0);
        assertFalse("LANG-677", DateUtils.isSameLocalTime(cal3, cal4));
        
        cal2.set(2004, 6, 9, 11, 45, 0);
        assertEquals(false, DateUtils.isSameLocalTime(cal1, cal2));
        try {
            DateUtils.isSameLocalTime((Calendar) null, (Calendar) null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }
}
