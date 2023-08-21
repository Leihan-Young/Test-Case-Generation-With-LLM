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
 * -------------------------
 * TimePeriodValueTests.java
 * -------------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 30-Jul-2003 : Version 1 (DG);
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 * 07-Apr-2008 : Added new tests for min/max-start/middle/end
 *               index updates (DG);
 *
 */
package org.jfree.data.time.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.MonthConstants;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.Year;
/**
 * A collection of test cases for the {@link TimePeriodValues} class.
 */
public class TimePeriodValuesTests extends TestCase {
    private TimePeriodValues seriesA;
    private TimePeriodValues seriesB;
    private TimePeriodValues seriesC;
    /**
     * Some tests for the getMaxMiddleIndex() method.
     */
    public void testGetMaxMiddleIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(150L, 200L), 4.0);
        assertEquals(1, s.getMaxMiddleIndex());
    }
}
