/*
 *  Copyright 2001-2012 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.tz.DateTimeZoneBuilder;
/**
 * This class is a JUnit test for DateTimeZone.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeZoneCutover extends TestCase {
    private void doTest_getOffsetFromLocal_Gaza(int days, int hour, int min, String expected) {
        DateTime dt = new DateTime(2007, 4, 1, hour, min, 0, 0, DateTimeZone.UTC).plusDays(days);
        int offset = MOCK_GAZA.getOffsetFromLocal(dt.getMillis());
        DateTime res = new DateTime(dt.getMillis() - offset, MOCK_GAZA);
        assertEquals(res.toString(), expected, res.toString());
    }
    private void doTest_getOffsetFromLocal_Turk(int days, int hour, int min, String expected) {
        DateTime dt = new DateTime(2007, 4, 1, hour, min, 0, 0, DateTimeZone.UTC).plusDays(days);
        int offset = MOCK_TURK.getOffsetFromLocal(dt.getMillis());
        DateTime res = new DateTime(dt.getMillis() - offset, MOCK_TURK);
        assertEquals(res.toString(), expected, res.toString());
    }
    private void doTest_getOffsetFromLocal(int month, int day, int hour, int min, String expected, DateTimeZone zone) {
        doTest_getOffsetFromLocal(2007, month, day, hour, min, 0, 0, expected, zone);
    }
    private void doTest_getOffsetFromLocal(int month, int day, int hour, int min, int sec, int milli, String expected, DateTimeZone zone) {
        doTest_getOffsetFromLocal(2007, month, day, hour, min, sec, milli, expected, zone);
    }
    private void doTest_getOffsetFromLocal(int year, int month, int day, int hour, int min, String expected, DateTimeZone zone) {
        doTest_getOffsetFromLocal(year, month, day, hour, min, 0, 0, expected, zone);
    }
    private void doTest_getOffsetFromLocal(int year, int month, int day, int hour, int min, int sec, int milli, String expected, DateTimeZone zone) {
        DateTime dt = new DateTime(year, month, day, hour, min, sec, milli, DateTimeZone.UTC);
        int offset = zone.getOffsetFromLocal(dt.getMillis());
        DateTime res = new DateTime(dt.getMillis() - offset, zone);
        assertEquals(res.toString(), expected, res.toString());
    }
    public void testBug3476684_adjustOffset() {
        final DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime base = new DateTime(2012, 2, 25, 22, 15, zone);
        DateTime baseBefore = base.plusHours(1);  // 23:15 (first)
        DateTime baseAfter = base.plusHours(2);  // 23:15 (second)
        
        assertSame(base, base.withEarlierOffsetAtOverlap());
        assertSame(base, base.withLaterOffsetAtOverlap());
        
        assertSame(baseBefore, baseBefore.withEarlierOffsetAtOverlap());
        assertEquals(baseAfter, baseBefore.withLaterOffsetAtOverlap());
        
        assertSame(baseAfter, baseAfter.withLaterOffsetAtOverlap());
        assertEquals(baseBefore, baseAfter.withEarlierOffsetAtOverlap());
    }
}
