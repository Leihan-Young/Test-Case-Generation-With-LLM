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
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.TimeZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * TestCase for DurationFormatUtils.
 * 
 * @author Apache Ant - DateUtilsTest
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author Stephen Colebourne
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @author Henri Yandell
 */
public class DurationFormatUtilsTest extends TestCase {
    private void assertArrayEquals(DurationFormatUtils.Token[] obj1, DurationFormatUtils.Token[] obj2) {
        assertEquals("Arrays are unequal length. ", obj1.length, obj2.length);
        for (int i = 0; i < obj1.length; i++) {
            assertTrue("Index " + i + " not equal, " + obj1[i] + " vs " + obj2, obj1[i].equals(obj2[i]));
        }
    }
    public void testJiraLang281() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.YEAR, 2005);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.MONTH, Calendar.OCTOBER);
        cal2.set(Calendar.DAY_OF_MONTH, 6);
        cal2.set(Calendar.YEAR, 2006);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        String result = DurationFormatUtils.formatPeriod(cal.getTime().getTime(), cal2.getTime().getTime(), "MM");
        assertEquals("09", result);
    }
}
