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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.apache.commons.lang3.SerializationUtils;
/**
 * Unit tests {@link org.apache.commons.lang3.time.FastDateFormat}.
 *
 * @since 2.0
 * @version $Id$
 */
public class FastDateFormatTest extends TestCase {
    public void testFormat() {
        Locale realDefaultLocale = Locale.getDefault();
        TimeZone realDefaultZone = TimeZone.getDefault();
        try {
            Locale.setDefault(Locale.US);
            TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

            GregorianCalendar cal1 = new GregorianCalendar(2003, 0, 10, 15, 33, 20);
            GregorianCalendar cal2 = new GregorianCalendar(2003, 6, 10, 9, 00, 00);
            Date date1 = cal1.getTime();
            Date date2 = cal2.getTime();
            long millis1 = date1.getTime();
            long millis2 = date2.getTime();

            FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            assertEquals(sdf.format(date1), fdf.format(date1));
            assertEquals("2003-01-10T15:33:20", fdf.format(date1));
            assertEquals("2003-01-10T15:33:20", fdf.format(cal1));
            assertEquals("2003-01-10T15:33:20", fdf.format(millis1));
            assertEquals("2003-07-10T09:00:00", fdf.format(date2));
            assertEquals("2003-07-10T09:00:00", fdf.format(cal2));
            assertEquals("2003-07-10T09:00:00", fdf.format(millis2));

            fdf = FastDateFormat.getInstance("Z");
            assertEquals("-0500", fdf.format(date1));
            assertEquals("-0500", fdf.format(cal1));
            assertEquals("-0500", fdf.format(millis1));

            assertEquals("-0400", fdf.format(date2));
            assertEquals("-0400", fdf.format(cal2));
            assertEquals("-0400", fdf.format(millis2));

            fdf = FastDateFormat.getInstance("ZZ");
            assertEquals("-05:00", fdf.format(date1));
            assertEquals("-05:00", fdf.format(cal1));
            assertEquals("-05:00", fdf.format(millis1));

            assertEquals("-04:00", fdf.format(date2));
            assertEquals("-04:00", fdf.format(cal2));
            assertEquals("-04:00", fdf.format(millis2));

            String pattern = "GGGG GGG GG G yyyy yyy yy y MMMM MMM MM M" +
                " dddd ddd dd d DDDD DDD DD D EEEE EEE EE E aaaa aaa aa a zzzz zzz zz z";
            fdf = FastDateFormat.getInstance(pattern);
            sdf = new SimpleDateFormat(pattern);
            // SDF bug fix starting with Java 7
            assertEquals(sdf.format(date1).replaceAll("2003 03 03 03", "2003 2003 03 2003"), fdf.format(date1));
            assertEquals(sdf.format(date2).replaceAll("2003 03 03 03", "2003 2003 03 2003"), fdf.format(date2));
        } finally {
            Locale.setDefault(realDefaultLocale);
            TimeZone.setDefault(realDefaultZone);
        }
    }
}
