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
  public void testIssue925() {
    test(
        "if (x[--y] === 1) {\n" +
        "    x[y] = 0;\n" +
        "} else {\n" +
        "    x[y] = 1;\n" +
        "}",
        "(x[--y] === 1) ? x[y] = 0 : x[y] = 1;");

    test(
        "if (x[--y]) {\n" +
        "    a = 0;\n" +
        "} else {\n" +
        "    a = 1;\n" +
        "}",
        "a = (x[--y]) ? 0 : 1;");

    test("if (x++) { x += 2 } else { x += 3 }",
         "x++ ? x += 2 : x += 3");

    test("if (x++) { x = x + 2 } else { x = x + 3 }",
        "x = x++ ? x + 2 : x + 3");
  }
}
