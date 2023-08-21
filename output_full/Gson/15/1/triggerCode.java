/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.stream;
import junit.framework.TestCase;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
public final class JsonWriterTest extends TestCase {
  public void testNonFiniteDoublesWhenLenient() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    jsonWriter.setLenient(true);
    jsonWriter.beginArray();
    jsonWriter.value(Double.NaN);
    jsonWriter.value(Double.NEGATIVE_INFINITY);
    jsonWriter.value(Double.POSITIVE_INFINITY);
    jsonWriter.endArray();
    assertEquals("[NaN,-Infinity,Infinity]", stringWriter.toString());
  }
}
