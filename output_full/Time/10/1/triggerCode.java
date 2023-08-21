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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * This class is a Junit unit test for Days.
 *
 * @author Stephen Colebourne
 */
public class TestDays extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    public void testFactory_daysBetween_RPartial_MonthDay() {
        MonthDay start1 = new MonthDay(2, 1);
        MonthDay start2 = new MonthDay(2, 28);
        MonthDay end1 = new MonthDay(2, 28);
        MonthDay end2 = new MonthDay(2, 29);
        
        assertEquals(27, Days.daysBetween(start1, end1).getDays());
        assertEquals(28, Days.daysBetween(start1, end2).getDays());
        assertEquals(0, Days.daysBetween(start2, end1).getDays());
        assertEquals(1, Days.daysBetween(start2, end2).getDays());
        
        assertEquals(-27, Days.daysBetween(end1, start1).getDays());
        assertEquals(-28, Days.daysBetween(end2, start1).getDays());
        assertEquals(0, Days.daysBetween(end1, start2).getDays());
        assertEquals(-1, Days.daysBetween(end2, start2).getDays());
    }
}
