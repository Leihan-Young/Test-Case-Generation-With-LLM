/*
 * Copyright 2011 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.javascript.jscomp;
/**
 * Unit tests for {@link ProcessCommonJSModules}
 */
public class ProcessCommonJSModulesTest extends CompilerTestCase {
  public void testGuessModuleName() {
    ProcessCommonJSModules pass = new ProcessCommonJSModules(null, "foo");
    assertEquals("module$baz", pass.guessCJSModuleName("foo/baz.js"));
    assertEquals("module$baz", pass.guessCJSModuleName("foo\\baz.js"));
    assertEquals("module$bar$baz", pass.guessCJSModuleName("foo\\bar\\baz.js"));
  }
}
