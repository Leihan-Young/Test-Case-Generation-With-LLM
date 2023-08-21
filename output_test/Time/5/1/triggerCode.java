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
package org.joda.time;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.base.BasePeriod;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
/**
 * This class is a Junit unit test for Duration.
 *
 * @author Stephen Colebourne
 */
public class TestPeriod_Basics extends TestCase {
    //private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private DateTimeZone originalDateTimeZone = null;
    private TimeZone originalTimeZone = null;
    private Locale originalLocale = null;
    public void testNormalizedStandard_periodType_months1() {
        Period test = new Period(1, 15, 0, 0, 0, 0, 0, 0);
        Period result = test.normalizedStandard(PeriodType.months());
        assertEquals(new Period(1, 15, 0, 0, 0, 0, 0, 0), test);
        assertEquals(new Period(0, 27, 0, 0, 0, 0, 0, 0, PeriodType.months()), result);
    }
}
