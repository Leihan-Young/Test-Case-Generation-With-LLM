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
package org.apache.commons.collections4;
import static org.apache.commons.collections4.functors.EqualPredicate.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Tests for IteratorUtils.
 *
 * @version $Id$
 */
public class IteratorUtilsTest {
    private List<Integer> collectionA = null;
    private List<Integer> collectionEven = null;
    private List<Integer> collectionOdd = null;
    private Iterable<Integer> iterableA = null;
    private Collection<Integer> emptyCollection = new ArrayList<Integer>(1);
    /**
     * Gets an immutable Iterator operating on the elements ["a", "b", "c", "d"].
     */
    private Iterator<String> getImmutableIterator() {
        final List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableIterator(list.iterator());
    }
    /**
     * Gets an immutable ListIterator operating on the elements ["a", "b", "c", "d"].
     */
    private ListIterator<String> getImmutableListIterator() {
        final List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return IteratorUtils.unmodifiableListIterator(list.listIterator());
    }
    /**
     * creates an array of four Node instances, mocked by EasyMock.
     */
    private Node[] createNodes() {
        final Node node1 = createMock(Node.class);
        final Node node2 = createMock(Node.class);
        final Node node3 = createMock(Node.class);
        final Node node4 = createMock(Node.class);
        replay(node1);
        replay(node2);
        replay(node3);
        replay(node4);

        return new Node[]{node1, node2, node3, node4};
}
    /**
     * Creates a NodeList containing the specified nodes.
     */
    private NodeList createNodeList(final Node[] nodes) {
        return new NodeList() {
            public Node item(final int index) {
                return nodes[index];
            }
            public int getLength() {
                return nodes.length;
            }
        };
    }
    /**
     * Tests methods collatedIterator(...)
     */
    @Test
    public void testCollatedIterator() {
        try {
            IteratorUtils.collatedIterator(null, collectionOdd.iterator(), null);
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }

        try {
            IteratorUtils.collatedIterator(null, null, collectionEven.iterator());
            fail("expecting NullPointerException");
        } catch (NullPointerException npe) {
            // expected
        }

        // natural ordering
        Iterator<Integer> it = 
                IteratorUtils.collatedIterator(null, collectionOdd.iterator(), collectionEven.iterator());

        List<Integer> result = IteratorUtils.toList(it);
        assertEquals(12, result.size());

        List<Integer> combinedList = new ArrayList<Integer>();
        combinedList.addAll(collectionOdd);
        combinedList.addAll(collectionEven);
        Collections.sort(combinedList);

        assertEquals(combinedList, result);

        it = IteratorUtils.collatedIterator(null, collectionOdd.iterator(), emptyCollection.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(collectionOdd, result);

        final Comparator<Integer> reverseComparator =
                ComparatorUtils.reversedComparator(ComparatorUtils.<Integer>naturalComparator());

        Collections.reverse((List<Integer>) collectionOdd);
        Collections.reverse((List<Integer>) collectionEven);
        Collections.reverse(combinedList);

        it = IteratorUtils.collatedIterator(reverseComparator,
                                            collectionOdd.iterator(),
                                            collectionEven.iterator());
        result = IteratorUtils.toList(it);
        assertEquals(combinedList, result);
    }
}
