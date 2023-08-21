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
 * MinMaxCategoryRendererTests.java
 * --------------------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MinMaxCategoryRendererTests.java,v 1.1.2.1 2006/10/03 15:41:35 mungady Exp $
 *
 * Changes
 * -------
 * 22-Oct-2003 : Version 1 (DG);
 * 28-Sep-2007 : Added testEquals() method (DG);
 *
 */
package org.jfree.chart.renderer.category.junit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 * Tests for the {@link MinMaxCategoryRenderer} class.
 */
public class MinMaxCategoryRendererTests extends TestCase {
    /**
     * Check that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        MinMaxCategoryRenderer r1 = new MinMaxCategoryRenderer();
        MinMaxCategoryRenderer r2 = new MinMaxCategoryRenderer();
        assertEquals(r1, r2);
        
        r1.setDrawLines(true);
        assertFalse(r1.equals(r2));
        r2.setDrawLines(true);
        assertTrue(r1.equals(r2));
        
        r1.setGroupPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.yellow));
        assertFalse(r1.equals(r2));
        r2.setGroupPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.yellow));
        assertTrue(r1.equals(r2));
        
        r1.setGroupStroke(new BasicStroke(1.2f));
        assertFalse(r1.equals(r2));
        r2.setGroupStroke(new BasicStroke(1.2f));
        assertTrue(r1.equals(r2));
    }
}
