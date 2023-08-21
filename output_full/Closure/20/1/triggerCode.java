/*
 * Copyright 2004 The Closure Compiler Authors.
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
 * Tests for {@link PeepholeSubstituteAlternateSyntax} in isolation.
 * Tests for the interaction of multiple peephole passes are in
 * PeepholeIntegrationTest.
 */
public class PeepholeSubstituteAlternateSyntaxTest extends CompilerTestCase {
  private boolean late = true;
  private void foldSame(String js) {
    testSame(js);
  }
  private void fold(String js, String expected) {
    test(js, expected);
  }
  private void fold(String js, String expected, DiagnosticType warning) {
    test(js, expected, warning);
  }
  public void testSimpleFunctionCall() {
    test("var a = String(23)", "var a = '' + 23");
    test("var a = String('hello')", "var a = '' + 'hello'");
    testSame("var a = String('hello', bar());");
    testSame("var a = String({valueOf: function() { return 1; }});");
  }
}
