/*
 * Copyright 2007 Google Inc.
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
public class InlineGettersTest extends CompilerTestCase {
  /**
   * Helper for tests that expects definitions to remain unchanged, such
   * that {@code definitions+js} is coverted to {@code definitions+expected}.
   */
  private void testWithPrefix(String definitions, String js, String expected) {
    test(definitions + js, definitions + expected);
  }
  public void testIssue2508576_1() {
    // Method defined by an extern should be left alone.
    String externs = "function alert(a) {}";
    testSame(externs, "({a:alert,b:alert}).a(\"a\")", null);
  }
}
