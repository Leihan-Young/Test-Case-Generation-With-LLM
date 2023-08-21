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
 * ----------------------------------------
 * DefaultIntervalCategoryDatasetTests.java
 * ----------------------------------------
 * (C) Copyright 2007, 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 08-Mar-2007 : Version 1 (DG);
 * 25-Feb-2008 : Added new tests to check behaviour of an empty dataset (DG);
 * 
 */
package org.jfree.data.category.junit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
/**
 * Tests for the {@link DefaultIntervalCategoryDataset} class.
 */
public class DefaultIntervalCategoryDatasetTests extends TestCase {
    /**
     * Some checks for the getCategoryIndex() method.
     */
    public void testGetCategoryIndex() {
    	// check an empty dataset
    	DefaultIntervalCategoryDataset empty 
    	        = new DefaultIntervalCategoryDataset(new double[0][0], 
    	        		new double[0][0]);
    	assertEquals(-1, empty.getCategoryIndex("ABC"));
    }
}
