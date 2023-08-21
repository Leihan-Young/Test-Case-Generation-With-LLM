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
 * --------------------------------
 * DefaultCategoryDatasetTests.java
 * --------------------------------
 * (C) Copyright 2004, 2005, 2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 23-Mar-2004 : Version 1 (DG);
 * 08-Mar-2007 : Added testCloning() (DG);
 * 21-Nov-2007 : Added testBug1835955() method (DG);
 *
 */
package org.jfree.data.category.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 * Tests for the {@link DefaultCategoryDataset} class.
 */
public class DefaultCategoryDatasetTests extends TestCase {
    /**
     * A test for bug 1835955.
     */
    public void testBug1835955() {
    	DefaultCategoryDataset d = new DefaultCategoryDataset();
    	d.addValue(1.0, "R1", "C1");
    	d.addValue(2.0, "R2", "C2");
    	d.removeColumn("C2");
    	d.addValue(3.0, "R2", "C2");
    	assertEquals(3.0, d.getValue("R2", "C2").doubleValue(), EPSILON);
    }
}
