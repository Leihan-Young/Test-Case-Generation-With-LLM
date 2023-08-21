/*
 *  Copyright 2001-2011 Stephen Colebourne
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
/**
 * This class is a Junit unit test for DateTimeFormatterBuilder.
 *
 * @author Stephen Colebourne
 * @author Brian S O'Neill
 */
public class TestDateTimeFormatterBuilder extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");
    public void test_printParseZoneDawsonCreek() {  // clashes with shorter Dawson
        DateTimeFormatterBuilder bld = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm ").appendTimeZoneId();
        DateTimeFormatter f = bld.toFormatter();
        
        DateTime dt = new DateTime(2007, 3, 4, 12, 30, 0, DateTimeZone.forID("America/Dawson_Creek"));
        assertEquals("2007-03-04 12:30 America/Dawson_Creek", f.print(dt));
        assertEquals(dt, f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek"));
    }
}
