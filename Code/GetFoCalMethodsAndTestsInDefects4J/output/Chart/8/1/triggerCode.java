/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * --------------
 * WeekTests.java
 * --------------
 * (C) Copyright 2002-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 05-Apr-2002 : Version 1 (DG);
 * 26-Jun-2002 : Removed unnecessary imports (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 21-Oct-2003 : Added hashCode test (DG);
 * 06-Apr-2006 : Added testBug1448828() method (DG);
 * 01-Jun-2006 : Added testBug1498805() method (DG);
 * 11-Jul-2007 : Fixed bad time zone assumption (DG);
 * 28-Aug-2007 : Added test for constructor problem (DG);
 * 19-Dec-2007 : Set default locale for tests that are sensitive
 *               to the locale (DG);
 *
 */
package org.jfree.data.time.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
/**
 * Tests for the {@link Week} class.
 */
public class WeekTests extends TestCase {
    private Week w1Y1900;
    private Week w2Y1900;
    private Week w51Y9999;
    private Week w52Y9999;
    /**
     * A test for a problem in constructing a new Week instance.
     */
    public void testConstructor() {
        Locale savedLocale = Locale.getDefault();
        TimeZone savedZone = TimeZone.getDefault();
        Locale.setDefault(new Locale("da", "DK"));
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Copenhagen"));
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance(
                TimeZone.getDefault(), Locale.getDefault());

        // first day of week is monday
        assertEquals(Calendar.MONDAY, cal.getFirstDayOfWeek());
        cal.set(2007, Calendar.AUGUST, 26, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date t = cal.getTime();
        Week w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"));
        assertEquals(34, w.getWeek());

        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone("US/Detroit"));
        cal = (GregorianCalendar) Calendar.getInstance(TimeZone.getDefault());
        // first day of week is Sunday
        assertEquals(Calendar.SUNDAY, cal.getFirstDayOfWeek());
        cal.set(2007, Calendar.AUGUST, 26, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        t = cal.getTime();
        w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"));
        assertEquals(35, w.getWeek());
        w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"),
                new Locale("da", "DK"));
        assertEquals(34, w.getWeek());

        Locale.setDefault(savedLocale);
        TimeZone.setDefault(savedZone);
    }
}
