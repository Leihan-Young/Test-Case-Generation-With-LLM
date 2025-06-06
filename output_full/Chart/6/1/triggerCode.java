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
 * ShapeListTests.java
 * -------------------
 * (C) Copyright 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 17-Jun-2008 : Version 1, copied over from JCommon (DG);
 *
 */
package org.jfree.chart.util.junit;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jfree.chart.util.ShapeList;
/**
 * Some tests for the {@link ShapeList} class.
 */
public class ShapeListTests extends TestCase {
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        ShapeList l1 = new ShapeList();
        l1.setShape(0, new Rectangle(1, 2, 3, 4));
        l1.setShape(1, new Line2D.Double(1.0, 2.0, 3.0, 4.0));
        l1.setShape(2, null);

        ShapeList l2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(l1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
            		buffer.toByteArray()));
            l2 = (ShapeList) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(l1, l2);

    }
}
