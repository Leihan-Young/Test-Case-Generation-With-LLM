/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.functional;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.common.TestTypes;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;
/**
 * Functional test for Json serialization and deserialization for Maps
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class MapTest extends TestCase {
  private Gson gson;
  private <K, V> Map<K, V> newMap(K key1, V value1, K key2, V value2) {
    Map<K, V> result = new LinkedHashMap<K, V>();
    result.put(key1, value1);
    result.put(key2, value2);
    return result;
  }
  public void testMapDeserializationWithUnquotedLongKeys() {
    long longKey = 9876543210L;
    String json = String.format("{%d:\"456\"}", longKey);
    Type typeOfMap = new TypeToken<Map<Long, String>>() {}.getType();
    Map<Long, String> map = gson.fromJson(json, typeOfMap);
    assertEquals(1, map.size());
    assertTrue(map.containsKey(longKey));
    assertEquals("456", map.get(longKey));
  }
}
