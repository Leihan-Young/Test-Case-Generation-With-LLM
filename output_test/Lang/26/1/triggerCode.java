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
 * @author Sean Schofield
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Fredrik Westermarck
 * @since 2.0
 * @version $Id$
 */
public class FastDateFormatTest extends TestCase {
    public void testLang645() {
        Locale locale = new Locale("sv", "SE");

        Calendar cal = Calendar.getInstance();
        cal.set(2010, 0, 1, 12, 0, 0);
        Date d = cal.getTime();

        FastDateFormat fdf = FastDateFormat.getInstance("EEEE', week 'ww", locale);

        assertEquals("fredag, week 53", fdf.format(d));
    }
}
