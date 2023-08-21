/*
 *  Copyright 2001-2013 Stephen Colebourne
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
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.chrono.ISOChronology;
/**
 * This class is a JUnit test for MutableDateTime.
 *
 * @author Stephen Colebourne
 */
public class TestMutableDateTime_Adds extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private DateTimeZone originalDateTimeZone = null;
    private TimeZone originalTimeZone = null;
    private Locale originalLocale = null;
    public void testAddYears_int_dstOverlapWinter_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        test.addHours(1);
        assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
        test.addYears(0);
        assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
    }
}
