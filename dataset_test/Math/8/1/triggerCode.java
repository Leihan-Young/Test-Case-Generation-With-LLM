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
package org.apache.commons.math3.distribution;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;
/**
 * Test class for {@link DiscreteRealDistribution}.
 * 
 * @version $Id: DiscreteRealDistributionTest.java 161 2013-03-07 09:47:32Z wydrych $
 */
public class DiscreteRealDistributionTest {
    private final DiscreteRealDistribution testDistribution;
    @Test
    public void testIssue942() {
        List<Pair<Object,Double>> list = new ArrayList<Pair<Object, Double>>();
        list.add(new Pair<Object, Double>(new Object() {}, new Double(0)));
        list.add(new Pair<Object, Double>(new Object() {}, new Double(1)));
        Assert.assertEquals(1, new DiscreteDistribution<Object>(list).sample(1).length);
    }
}
