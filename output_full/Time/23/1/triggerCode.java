/*
 *  Copyright 2001-2006 Stephen Colebourne
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
    public void testForID_String_old() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("GMT", "UTC");
        map.put("WET", "WET");
        map.put("CET", "CET");
        map.put("MET", "CET");
        map.put("ECT", "CET");
        map.put("EET", "EET");
        map.put("MIT", "Pacific/Apia");
        map.put("HST", "Pacific/Honolulu");
        map.put("AST", "America/Anchorage");
        map.put("PST", "America/Los_Angeles");
        map.put("MST", "America/Denver");
        map.put("PNT", "America/Phoenix");
        map.put("CST", "America/Chicago");
        map.put("EST", "America/New_York");
        map.put("IET", "America/Indiana/Indianapolis");
        map.put("PRT", "America/Puerto_Rico");
        map.put("CNT", "America/St_Johns");
        map.put("AGT", "America/Argentina/Buenos_Aires");
        map.put("BET", "America/Sao_Paulo");
        map.put("ART", "Africa/Cairo");
        map.put("CAT", "Africa/Harare");
        map.put("EAT", "Africa/Addis_Ababa");
        map.put("NET", "Asia/Yerevan");
        map.put("PLT", "Asia/Karachi");
        map.put("IST", "Asia/Kolkata");
        map.put("BST", "Asia/Dhaka");
        map.put("VST", "Asia/Ho_Chi_Minh");
        map.put("CTT", "Asia/Shanghai");
        map.put("JST", "Asia/Tokyo");
        map.put("ACT", "Australia/Darwin");
        map.put("AET", "Australia/Sydney");
        map.put("SST", "Pacific/Guadalcanal");
        map.put("NST", "Pacific/Auckland");
        for (String key : map.keySet()) {
            String value = map.get(key);
            TimeZone juZone = TimeZone.getTimeZone(key);
            DateTimeZone zone = DateTimeZone.forTimeZone(juZone);
            assertEquals(value, zone.getID());
//            System.out.println(juZone);
//            System.out.println(juZone.getDisplayName());
//            System.out.println(zone);
//            System.out.println("------");
        }
    }
}
