/*
 *  Copyright 2001-2005 Stephen Colebourne
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
package org.joda.time.format;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
/**
 * This class is a Junit unit test for ISOPeriodFormat.
 *
 * @author Stephen Colebourne
 */
public class TestISOPeriodFormat extends TestCase {
    private static final Period PERIOD = new Period(1, 2, 3, 4, 5, 6, 7, 8);
    private static final Period EMPTY_PERIOD = new Period(0, 0, 0, 0, 0, 0, 0, 0);
    private static final Period YEAR_DAY_PERIOD = new Period(1, 0, 0, 4, 5, 6, 7, 8, PeriodType.yearDayTime());
    private static final Period EMPTY_YEAR_DAY_PERIOD = new Period(0, 0, 0, 0, 0, 0, 0, 0, PeriodType.yearDayTime());
    private static final Period TIME_PERIOD = new Period(0, 0, 0, 0, 5, 6, 7, 8);
    private static final Period DATE_PERIOD = new Period(1, 2, 3, 4, 0, 0, 0, 0);
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");
    private DateTimeZone originalDateTimeZone = null;
    private TimeZone originalTimeZone = null;
    private Locale originalLocale = null;
    public void testFormatStandard_negative() {
        Period p = new Period(-1, -2, -3, -4, -5, -6, -7, -8);
        assertEquals("P-1Y-2M-3W-4DT-5H-6M-7.008S", ISOPeriodFormat.standard().print(p));
        
        p = Period.years(-54);
        assertEquals("P-54Y", ISOPeriodFormat.standard().print(p));
        
        p = Period.seconds(4).withMillis(-8);
        assertEquals("PT3.992S", ISOPeriodFormat.standard().print(p));
        
        p = Period.seconds(-4).withMillis(8);
        assertEquals("PT-3.992S", ISOPeriodFormat.standard().print(p));
        
        p = Period.seconds(-23);
        assertEquals("PT-23S", ISOPeriodFormat.standard().print(p));
        
        p = Period.millis(-8);
        assertEquals("PT-0.008S", ISOPeriodFormat.standard().print(p));
    }
}
