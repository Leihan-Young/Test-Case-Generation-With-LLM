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
 * Verifies that valid candidates for object literals are inlined as
 * expected, and invalid candidates are not touched.
 *
 */
public class InlineObjectLiteralsTest extends CompilerTestCase {
  private void testLocal(String code, String result) {
    test(LOCAL_PREFIX + code + LOCAL_POSTFIX,
         LOCAL_PREFIX + result + LOCAL_POSTFIX);
  }
  private void testSameLocal(String code) {
    testLocal(code, code);
  }
  public void testNoInlineDeletedProperties() {
    testSameLocal(
        "var foo = {bar:1};" +
        "delete foo.bar;" +
        "return foo.bar;");
  }
}
