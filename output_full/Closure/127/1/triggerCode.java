/*
 * Copyright 2008 The Closure Compiler Authors.
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
 * Test for {@link UnreachableCodeElimination}.
 *
 */
public class UnreachableCodeEliminationTest extends CompilerTestCase {
  private boolean removeNoOpStatements = true;
  public void testIssue4177428_return() {
    test(
        "f = function() {\n" +
        "  var action;\n" +
        "  a: {\n" +
        "    var proto = null;\n" +
        "    try {\n" +
        "      proto = new Proto\n" +
        "    } finally {\n" +
        "      action = proto;\n" +
        "      return\n" +  // Keep this...
        "    }\n" +
        "  }\n" +
        "  alert(action)\n" + // and remove this.
        "};",
        "f = function() {\n" +
        "  var action;\n" +
        "  a: {\n" +
        "    var proto = null;\n" +
        "    try {\n" +
        "      proto = new Proto\n" +
        "    } finally {\n" +
        "      action = proto;\n" +
        "      return\n" +
        "    }\n" +
        "  }\n" +
        "};"
        );
  }
}
