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
package org.apache.commons.lang.time;
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
import org.apache.commons.lang.SerializationUtils;
/**
 * Unit tests {@link org.apache.commons.lang.time.FastDateFormat}.
 *
 * @author Sean Schofield
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Fredrik Westermarck
 * @since 2.0
 * @version $Id$
 */
public class FastDateFormatTest extends TestCase {
    public void test_changeDefault_Locale_DateInstance() {
        Locale realDefaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            FastDateFormat format1 = FastDateFormat.getDateInstance(FastDateFormat.FULL, Locale.GERMANY);
            FastDateFormat format2 = FastDateFormat.getDateInstance(FastDateFormat.FULL);
            Locale.setDefault(Locale.GERMANY);
            FastDateFormat format3 = FastDateFormat.getDateInstance(FastDateFormat.FULL);

            assertSame(Locale.GERMANY, format1.getLocale());
            assertSame(Locale.US, format2.getLocale());
            assertSame(Locale.GERMANY, format3.getLocale());
            assertTrue(format1 != format2); // -- junit 3.8 version -- assertFalse(format1 == format2);
            assertTrue(format2 != format3);

        } finally {
            Locale.setDefault(realDefaultLocale);
        }
    }
}
