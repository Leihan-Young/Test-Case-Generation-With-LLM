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
 * -------------------
 * Pie3DPlotTests.java
 * -------------------
 * (C) Copyright 2003-2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 * 22-Mar-2007 : Added testEquals() (DG);
 * 05-Oct-2007 : Modified testEquals() for new field (DG);
 * 19-Mar-2008 : Added test for null dataset (DG);
 *
 */
package org.jfree.chart.plot.junit;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
/**
 * Tests for the {@link PiePlot3D} class.
 */
public class PiePlot3DTests extends TestCase {
    /**
     * Draws a pie chart where the label generator returns null.
     */
    public void testDrawWithNullDataset() {
        JFreeChart chart = ChartFactory.createPieChart3D("Test", null, true, 
                false, false);
        boolean success = false;
        try {
            BufferedImage image = new BufferedImage(200 , 100, 
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 100), null, null);
            g2.dispose();
            success = true;
        }
        catch (Exception e) {
            success = false;
        }
        assertTrue(success);
    }
}
