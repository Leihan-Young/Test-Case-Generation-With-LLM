/*
 * Copyright (C) 2017 Gson Authors
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
package com.google.gson.internal.bind;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.$Gson$Types;
import junit.framework.TestCase;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
/**
 * Test fixes for infinite recursion on {@link $Gson$Types#resolve(java.lang.reflect.Type, Class,
 * java.lang.reflect.Type)}, described at <a href="https://github.com/google/gson/issues/440">Issue #440</a>
 * and similar issues.
 * <p>
 * These tests originally caused {@link StackOverflowError} because of infinite recursion on attempts to
 * resolve generics on types, with an intermediate types like 'Foo2&lt;? extends ? super ? extends ... ? extends A&gt;'
 */
public class RecursiveTypesResolveTest extends TestCase {
  public void testDoubleSupertype() {
    assertEquals($Gson$Types.supertypeOf(Number.class), $Gson$Types.supertypeOf($Gson$Types.supertypeOf(Number.class)));
  }
}
