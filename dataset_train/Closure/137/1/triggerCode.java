/*
 * Copyright 2009 Google Inc.
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
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.MakeDeclaredNamesUnique.InlineRenamer;
import com.google.javascript.rhino.Node;
/**
 * @author johnlenz@google.com (John Lenz)
 */
public class MakeDeclaredNamesUniqueTest extends CompilerTestCase {
  private boolean useDefaultRenamer = false;
  private boolean invert = false;
  private boolean removeConst = false;
  private final String localNamePrefix = "unique_";
  private String wrapInFunction(String s) {
    return "function f(){" + s + "}";
  }
  public void testOnlyInversion3() {
    invert = true;
    test(
        "function x1() {" +
        "  var a$$1;" +
        "  function x2() {" +
        "    var a$$2;" +
        "  }" +
        "  function x3() {" +
        "    var a$$3;" +
        "  }" +
        "}",
        "function x1() {" +
        "  var a$$1;" +
        "  function x2() {" +
        "    var a;" +
        "  }" +
        "  function x3() {" +
        "    var a;" +
        "  }" +
        "}");
  }
}
