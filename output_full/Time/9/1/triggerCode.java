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
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.text.DateFormatSymbols;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.joda.time.tz.DefaultNameProvider;
import org.joda.time.tz.NameProvider;
import org.joda.time.tz.Provider;
import org.joda.time.tz.UTCProvider;
import org.joda.time.tz.ZoneInfoProvider;
/**
 * This class is a JUnit test for DateTimeZone.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeZone extends TestCase {
    private static final boolean OLD_JDK;
    public void testForOffsetHoursMinutes_int_int() {
        assertEquals(DateTimeZone.UTC, DateTimeZone.forOffsetHoursMinutes(0, 0));
        assertEquals(DateTimeZone.forID("+23:59"), DateTimeZone.forOffsetHoursMinutes(23, 59));
        assertEquals(DateTimeZone.forID("+03:15"), DateTimeZone.forOffsetHoursMinutes(3, 15));
        assertEquals(DateTimeZone.forID("-02:00"), DateTimeZone.forOffsetHoursMinutes(-2, 0));
        assertEquals(DateTimeZone.forID("-02:30"), DateTimeZone.forOffsetHoursMinutes(-2, 30));
        assertEquals(DateTimeZone.forID("-23:59"), DateTimeZone.forOffsetHoursMinutes(-23, 59));
        try {
            DateTimeZone.forOffsetHoursMinutes(2, 60);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            DateTimeZone.forOffsetHoursMinutes(-2, 60);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            DateTimeZone.forOffsetHoursMinutes(2, -1);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            DateTimeZone.forOffsetHoursMinutes(-2, -1);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            DateTimeZone.forOffsetHoursMinutes(24, 0);
            fail();
        } catch (IllegalArgumentException ex) {}
        try {
            DateTimeZone.forOffsetHoursMinutes(-24, 0);
            fail();
        } catch (IllegalArgumentException ex) {}
    }        
}
