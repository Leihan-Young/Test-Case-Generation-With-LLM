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
package org.apache.commons.collections4.trie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import junit.framework.Test;
import org.apache.commons.collections4.BulkTest;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.map.AbstractSortedMapTest;
import org.junit.Assert;
/**
 * JUnit tests for the PatriciaTrie.
 *
 * @since 4.0
 * @version $Id$
 */
public class PatriciaTrieTest<V> extends AbstractSortedMapTest<String, V> {
    public void testPrefixMapClear() {
        Trie<String, Integer> trie = new PatriciaTrie<Integer>();
        trie.put("Anna", 1);
        trie.put("Anael", 2);
        trie.put("Analu", 3);
        trie.put("Andreas", 4);
        trie.put("Andrea", 5);
        trie.put("Andres", 6);
        trie.put("Anatole", 7);
        SortedMap<String, Integer> prefixMap = trie.prefixMap("And");
        assertEquals(new HashSet<String>(Arrays.asList("Andrea", "Andreas", "Andres")), prefixMap.keySet());
        assertEquals(Arrays.asList(5, 4, 6), new ArrayList<Integer>(prefixMap.values()));

        prefixMap.clear();
        assertTrue(prefixMap.isEmpty());
        assertTrue(prefixMap.keySet().isEmpty());
        assertTrue(prefixMap.values().isEmpty());
        assertEquals(new HashSet<String>(Arrays.asList("Anael", "Analu", "Anatole", "Anna")), trie.keySet());
        assertEquals(Arrays.asList(2, 3, 7, 1), new ArrayList<Integer>(trie.values()));
    }
}
