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
package org.apache.commons.collections4.map;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.collections4.AbstractObjectTest;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiMap;
/**
 * TestMultiValueMap.
 *
 * @since 3.2
 * @version $Id$
 */
public class MultiValueMapTest<K, V> extends AbstractObjectTest {
    @SuppressWarnings("unchecked")
    private MultiValueMap<K, V> createTestMap() {
        return createTestMap(ArrayList.class);
    }
    @SuppressWarnings("unchecked")
    private <C extends Collection<V>> MultiValueMap<K, V> createTestMap(final Class<C> collectionClass) {
        final MultiValueMap<K, V> map = MultiValueMap.multiValueMap(new HashMap<K, C>(), collectionClass);
        map.put((K) "one", (V) "uno");
        map.put((K) "one", (V) "un");
        map.put((K) "two", (V) "dos");
        map.put((K) "two", (V) "deux");
        map.put((K) "three", (V) "tres");
        map.put((K) "three", (V) "trois");
        return map;
    }
    private byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(object);
        oos.close();

        return baos.toByteArray();
    }
    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream iis = new ObjectInputStream(bais);
        
        return iis.readObject();
    }
    @SuppressWarnings("rawtypes")
    private Map makeEmptyMap() {
        return new MultiValueMap();
    }
    public void testUnsafeDeSerialization() throws Exception {
        MultiValueMap map1 = MultiValueMap.multiValueMap(new HashMap(), ArrayList.class);
        byte[] bytes = serialize(map1);
        Object result = deserialize(bytes);
        assertEquals(map1, result);
        
        MultiValueMap map2 = MultiValueMap.multiValueMap(new HashMap(), (Class) String.class);
        bytes = serialize(map2);
        try {
            result = deserialize(bytes);
            fail("unsafe clazz accepted when de-serializing MultiValueMap");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }
}
