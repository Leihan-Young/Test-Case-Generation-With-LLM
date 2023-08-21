/*
 * Copyright (C) 2014 Google Inc.
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
package com.google.gson.functional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import junit.framework.TestCase;
/**
 * Functional tests for the {@link com.google.gson.annotations.JsonAdapter} annotation on fields.
 */
public final class JsonAdapterAnnotationOnFieldsTest extends TestCase {
  /** Regression test contributed through https://github.com/google/gson/issues/831 */
  public void testPrimitiveFieldAnnotationTakesPrecedenceOverDefault() {
    Gson gson = new Gson();
    String json = gson.toJson(new GadgetWithPrimitivePart(42));
    assertEquals("{\"part\":\"42\"}", json);
    GadgetWithPrimitivePart gadget = gson.fromJson(json, GadgetWithPrimitivePart.class);
    assertEquals(42, gadget.part);
  }
}
