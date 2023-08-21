/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.linear;
import java.io.Serializable;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.TestUtils;
import org.apache.commons.math.analysis.UnivariateRealFunction;
/**
 * Test cases for the {@link ArrayRealVector} class.
 *
 * @version $Revision$ $Date$
 */
public class ArrayRealVectorTest extends TestCase {
        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Not supported, unneeded for test purposes");
        }
    public void testBasicFunctions() {
        ArrayRealVector v1 = new ArrayRealVector(vec1);
        ArrayRealVector v2 = new ArrayRealVector(vec2);
        ArrayRealVector v5 = new ArrayRealVector(vec5);
        ArrayRealVector v_null = new ArrayRealVector(vec_null);

        RealVectorTestImpl v2_t = new RealVectorTestImpl(vec2);

        // emacs calc: [-4, 0, 3, 1, -6, 3] A --> 8.4261497731763586307
        double d_getNorm = v5.getNorm();
        assertEquals("compare values  ", 8.4261497731763586307, d_getNorm);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vN --> 17
        double d_getL1Norm = v5.getL1Norm();
        assertEquals("compare values  ", 17.0, d_getL1Norm);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vn --> 6
        double d_getLInfNorm = v5.getLInfNorm();
        assertEquals("compare values  ", 6.0, d_getLInfNorm);


        //octave =  sqrt(sumsq(v1-v2))
        double dist = v1.getDistance(v2);
        assertEquals("compare values  ",v1.subtract(v2).getNorm(), dist );

        //octave =  sqrt(sumsq(v1-v2))
        double dist_2 = v1.getDistance(v2_t);
        assertEquals("compare values  ", v1.subtract(v2).getNorm(),dist_2 );

        //octave =  sqrt(sumsq(v1-v2))
        double dist_3 = v1.getDistance((RealVector) v2);
        assertEquals("compare values  ", v1.subtract(v2).getNorm(),dist_3 );

        //octave =  ???
        double d_getL1Distance = v1. getL1Distance(v2);
        assertEquals("compare values  ",9d, d_getL1Distance );

        double d_getL1Distance_2 = v1. getL1Distance(v2_t);
        assertEquals("compare values  ",9d, d_getL1Distance_2 );

        double d_getL1Distance_3 = v1. getL1Distance((RealVector) v2);
        assertEquals("compare values  ",9d, d_getL1Distance_3 );

        //octave =  ???
        double d_getLInfDistance = v1. getLInfDistance(v2);
        assertEquals("compare values  ",3d, d_getLInfDistance );

        double d_getLInfDistance_2 = v1. getLInfDistance(v2_t);
        assertEquals("compare values  ",3d, d_getLInfDistance_2 );

        double d_getLInfDistance_3 = v1. getLInfDistance((RealVector) v2);
        assertEquals("compare values  ",3d, d_getLInfDistance_3 );

        //octave =  v1 + v2
        ArrayRealVector v_add = v1.add(v2);
        double[] result_add = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add.getData(),result_add,normTolerance);

        RealVectorTestImpl vt2 = new RealVectorTestImpl(vec2);
        RealVector v_add_i = v1.add(vt2);
        double[] result_add_i = {5d, 7d, 9d};
        assertClose("compare vect" ,v_add_i.getData(),result_add_i,normTolerance);

        //octave =  v1 - v2
        ArrayRealVector v_subtract = v1.subtract(v2);
        double[] result_subtract = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract.getData(),result_subtract,normTolerance);

        RealVector v_subtract_i = v1.subtract(vt2);
        double[] result_subtract_i = {-3d, -3d, -3d};
        assertClose("compare vect" ,v_subtract_i.getData(),result_subtract_i,normTolerance);

        // octave v1 .* v2
        ArrayRealVector  v_ebeMultiply = v1.ebeMultiply(v2);
        double[] result_ebeMultiply = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply.getData(),result_ebeMultiply,normTolerance);

        RealVector  v_ebeMultiply_2 = v1.ebeMultiply(v2_t);
        double[] result_ebeMultiply_2 = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply_2.getData(),result_ebeMultiply_2,normTolerance);

        RealVector  v_ebeMultiply_3 = v1.ebeMultiply((RealVector) v2);
        double[] result_ebeMultiply_3 = {4d, 10d, 18d};
        assertClose("compare vect" ,v_ebeMultiply_3.getData(),result_ebeMultiply_3,normTolerance);

        // octave v1 ./ v2
        ArrayRealVector  v_ebeDivide = v1.ebeDivide(v2);
        double[] result_ebeDivide = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide.getData(),result_ebeDivide,normTolerance);

        RealVector  v_ebeDivide_2 = v1.ebeDivide(v2_t);
        double[] result_ebeDivide_2 = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide_2.getData(),result_ebeDivide_2,normTolerance);

        RealVector  v_ebeDivide_3 = v1.ebeDivide((RealVector) v2);
        double[] result_ebeDivide_3 = {0.25d, 0.4d, 0.5d};
        assertClose("compare vect" ,v_ebeDivide_3.getData(),result_ebeDivide_3,normTolerance);

        // octave  dot(v1,v2)
        double dot =  v1.dotProduct(v2);
        assertEquals("compare val ",32d, dot);

        // octave  dot(v1,v2_t)
        double dot_2 =  v1.dotProduct(v2_t);
        assertEquals("compare val ",32d, dot_2);

        RealMatrix m_outerProduct = v1.outerProduct(v2);
        assertEquals("compare val ",4d, m_outerProduct.getEntry(0,0));

        RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
        assertEquals("compare val ",4d, m_outerProduct_2.getEntry(0,0));

        RealMatrix m_outerProduct_3 = v1.outerProduct((RealVector) v2);
        assertEquals("compare val ",4d, m_outerProduct_3.getEntry(0,0));

        RealVector v_unitVector = v1.unitVector();
        RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
        assertClose("compare vect" ,v_unitVector.getData(),v_unitVector_2.getData(),normTolerance);

        try {
            v_null.unitVector();
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }

        ArrayRealVector v_unitize = (ArrayRealVector)v1.copy();
        v_unitize.unitize();
        assertClose("compare vect" ,v_unitVector_2.getData(),v_unitize.getData(),normTolerance);
        try {
            v_null.unitize();
            fail("Expecting ArithmeticException");
        } catch (ArithmeticException ex) {
            // expected behavior
        } catch (Exception e) {
            fail("wrong exception caught");
        }

        ArrayRealVector v_projection = v1.projection(v2);
        double[] result_projection = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection.getData(), result_projection, normTolerance);

        RealVector v_projection_2 = v1.projection(v2_t);
        double[] result_projection_2 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_2.getData(), result_projection_2, normTolerance);

        RealVector v_projection_3 = v1.projection(v2.getData());
        double[] result_projection_3 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_3.getData(), result_projection_3, normTolerance);

    }
}
