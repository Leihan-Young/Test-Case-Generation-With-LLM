/*
 * Copyright 2008 Google Inc.
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
import com.google.javascript.rhino.Node;
/**
 * Unit tests for {@link CoalesceVariableNames}
 *
*
 */
public class CoalesceVariableNamesTest extends CompilerTestCase {
  private boolean usePseudoName = false;
  private void inFunction(String src) {
    inFunction(src, src);
  }
  private void inFunction(String src, String expected) {
    test("function FUNC(){" + src + "}",
         "function FUNC(){" + expected + "}");
  }
  private void test(String src) {
    test(src, src);
  }
  public void testParameter4() {
    // Make sure that we do not merge two-arg functions because of the
    // IE sort bug (see comments in computeEscaped)
    test("function FUNC(x, y) {var a,b; y; a=0; a; x; b=0; b}",
         "function FUNC(x, y) {var a; y; a=0; a; x; a=0; a}");
  }
}
