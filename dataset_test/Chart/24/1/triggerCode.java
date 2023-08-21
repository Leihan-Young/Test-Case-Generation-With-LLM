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
 * ------------------------
 * GrayPaintScaleTests.java
 * ------------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: GrayPaintScaleTests.java,v 1.1.2.1 2007/01/31 14:15:16 mungady Exp $
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 26-Sep-2007 : Added testConstructor() and testGetPaint() (DG);
 *
 */
package org.jfree.chart.renderer.junit;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.renderer.GrayPaintScale;
/**
 * Tests for the {@link GrayPaintScale} class.
 */
public class GrayPaintScaleTests extends TestCase {
    /**
     * Some checks for the getPaint() method.
     */
    public void testGetPaint() {
        GrayPaintScale gps = new GrayPaintScale();
        Color c = (Color) gps.getPaint(0.0);
        assertTrue(c.equals(Color.black));
        c = (Color) gps.getPaint(1.0);
        assertTrue(c.equals(Color.white));
        
        // check lookup values that are outside the bounds - see bug report
        // 1767315
        c = (Color) gps.getPaint(-0.5);
        assertTrue(c.equals(Color.black));
        c = (Color) gps.getPaint(1.5);
        assertTrue(c.equals(Color.white));
    }
}
