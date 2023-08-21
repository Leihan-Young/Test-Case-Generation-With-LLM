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
package org.joda.time.chrono;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
/**
 * Test.
 */
public class TestGJDate extends TestCase {
    public void test_cutoverPreZero() {
        DateTime cutover = new LocalDate(-2, 6, 30, ISOChronology.getInstanceUTC()).toDateTimeAtStartOfDay(DateTimeZone.UTC);
        try {
            GJChronology.getInstance(DateTimeZone.UTC, cutover);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
