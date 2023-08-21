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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
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
    public void testLang538() {
        final String dateTime = "2009-10-16T16:42:16.000Z";

        // more commonly constructed with: cal = new GregorianCalendar(2009, 9, 16, 8, 42, 16)
        // for the unit test to work in any time zone, constructing with GMT-8 rather than default locale time zone
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-8"));
        cal.clear();
        cal.set(2009, 9, 16, 8, 42, 16);

        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("GMT"));
        assertEquals("dateTime", dateTime, format.format(cal));
    }
}
