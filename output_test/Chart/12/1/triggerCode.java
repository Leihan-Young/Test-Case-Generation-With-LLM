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
 * MultiplePiePlotTests.java
 * -------------------------
 * (C) Copyright 2005-2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 16-Jun-2005 : Version 1 (DG);
 * 06-Apr-2006 : Added tests for new fields (DG);
 * 21-Jun-2007 : Removed JCommon dependencies (DG);
 * 18-Apr-2008 : Added testConstructor() (DG);
 *
 */
package org.jfree.chart.plot.junit;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 * Some tests for the {@link MultiplePiePlot} class.
 */
public class MultiplePiePlotTests extends TestCase
        implements PlotChangeListener {
    /**
     * Some checks for the constructors.
     */
    public void testConstructor() {
    	MultiplePiePlot plot = new MultiplePiePlot();
    	assertNull(plot.getDataset());

    	// the following checks that the plot registers itself as a listener
    	// with the dataset passed to the constructor - see patch 1943021
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	plot = new MultiplePiePlot(dataset);
    	assertTrue(dataset.hasListener(plot));
    }
}
