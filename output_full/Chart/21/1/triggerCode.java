/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * ---------------------------------------------
 * DefaultBoxAndWhiskerCategoryDatasetTests.java
 * ---------------------------------------------
 * (C) Copyright 2004-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 01-Mar-2004 : Version 1 (DG);
 * 17-Apr-2007 : Added a test for bug 1701822 (DG);
 * 28-Sep-2007 : Enhanced testClone() (DG);
 * 02-Oct-2007 : Added new tests (DG);
 * 03-Oct-2007 : Added getTestRangeBounds() (DG);
 * 
 */
package org.jfree.data.statistics.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
/**
 * Tests for the {@link DefaultBoxAndWhiskerCategoryDataset} class.
 */
public class DefaultBoxAndWhiskerCategoryDatasetTests extends TestCase {
    /**
     * Some checks for the getRangeBounds() method.
     */
    public void testGetRangeBounds() {
        DefaultBoxAndWhiskerCategoryDataset d1 
                = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(new BoxAndWhiskerItem(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 
                new ArrayList()), "R1", "C1");
        assertEquals(new Range(7.0, 8.0), d1.getRangeBounds(false));
        assertEquals(new Range(7.0, 8.0), d1.getRangeBounds(true));
        
        d1.add(new BoxAndWhiskerItem(1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 
                new ArrayList()), "R1", "C1");
        assertEquals(new Range(7.5, 8.5), d1.getRangeBounds(false));
        assertEquals(new Range(7.5, 8.5), d1.getRangeBounds(true));
        
        d1.add(new BoxAndWhiskerItem(2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5, 
                new ArrayList()), "R2", "C1");
        assertEquals(new Range(7.5, 9.5), d1.getRangeBounds(false));
        assertEquals(new Range(7.5, 9.5), d1.getRangeBounds(true));
        
        // this replaces the entry with the current minimum value, but the new
        // minimum value is now in a different item
        d1.add(new BoxAndWhiskerItem(1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 8.6, 9.6, 
                new ArrayList()), "R1", "C1");
        assertEquals(new Range(8.5, 9.6), d1.getRangeBounds(false));
        assertEquals(new Range(8.5, 9.6), d1.getRangeBounds(true));
    }
}
