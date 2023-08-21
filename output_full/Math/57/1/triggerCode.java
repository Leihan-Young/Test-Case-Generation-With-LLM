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
package org.apache.commons.math.stat.clustering;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
public class KMeansPlusPlusClustererTest {
    /**
     * Test points that are very close together. See issue MATH-546.
     */
    @Test
    public void testSmallDistances() {
        // Create a bunch of CloseIntegerPoints. Most are identical, but one is different by a
        // small distance.
        int[] repeatedArray = { 0 };
        int[] uniqueArray = { 1 };
        CloseIntegerPoint repeatedPoint =
            new CloseIntegerPoint(new EuclideanIntegerPoint(repeatedArray));
        CloseIntegerPoint uniquePoint =
            new CloseIntegerPoint(new EuclideanIntegerPoint(uniqueArray));

        Collection<CloseIntegerPoint> points = new ArrayList<CloseIntegerPoint>();
        final int NUM_REPEATED_POINTS = 10 * 1000;
        for (int i = 0; i < NUM_REPEATED_POINTS; ++i) {
            points.add(repeatedPoint);
        }
        points.add(uniquePoint);

        // Ask a KMeansPlusPlusClusterer to run zero iterations (i.e., to simply choose initial
        // cluster centers).
        final long RANDOM_SEED = 0;
        final int NUM_CLUSTERS = 2;
        final int NUM_ITERATIONS = 0;
        KMeansPlusPlusClusterer<CloseIntegerPoint> clusterer =
            new KMeansPlusPlusClusterer<CloseIntegerPoint>(new Random(RANDOM_SEED));
        List<Cluster<CloseIntegerPoint>> clusters =
            clusterer.cluster(points, NUM_CLUSTERS, NUM_ITERATIONS);

        // Check that one of the chosen centers is the unique point.
        boolean uniquePointIsCenter = false;
        for (Cluster<CloseIntegerPoint> cluster : clusters) {
            if (cluster.getCenter().equals(uniquePoint)) {
                uniquePointIsCenter = true;
            }
        }
        assertTrue(uniquePointIsCenter);
    }
}
