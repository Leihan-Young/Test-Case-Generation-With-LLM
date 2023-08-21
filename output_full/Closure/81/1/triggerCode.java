/*
 * Copyright 2007 The Closure Compiler Authors.
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
package com.google.javascript.jscomp.parsing;
import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.mozilla.rhino.ScriptRuntime;
import com.google.javascript.jscomp.testing.TestErrorReporter;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.testing.BaseJSTypeTestCase;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;
public class ParserTest extends BaseJSTypeTestCase {
  private boolean es5mode;
  private void testLinenoCharnoBinop(String binop) {
    Node op = parse("var a = 89 " + binop + " 76").getFirstChild().
        getFirstChild().getFirstChild();

    assertEquals(1, op.getLineno());
    assertEquals(11, op.getCharno());
  }
  private void assertNodeEquality(Node expected, Node found) {
    String message = expected.checkTreeEquals(found);
    if (message != null) {
      fail(message);
    }
  }
  private Node createScript(Node n) {
    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(n);
    return script;
  }
  private void parseError(String string, String... errors) {
    TestErrorReporter testErrorReporter = new TestErrorReporter(errors, null);
    Node script = null;
    try {
      script = ParserRunner.parse(
          "input", string, ParserRunner.createConfig(true, es5mode, false),
          testErrorReporter, Logger.getAnonymousLogger());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // verifying that all warnings were seen
    assertTrue(testErrorReporter.hasEncounteredAllErrors());
    assertTrue(testErrorReporter.hasEncounteredAllWarnings());
  }
  private Node parse(String string, String... warnings) {
    TestErrorReporter testErrorReporter = new TestErrorReporter(null, warnings);
    Node script = null;
    try {
      script = ParserRunner.parse(
          "input", string, ParserRunner.createConfig(true, es5mode, false),
          testErrorReporter, Logger.getAnonymousLogger());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // verifying that all warnings were seen
    assertTrue(testErrorReporter.hasEncounteredAllErrors());
    assertTrue(testErrorReporter.hasEncounteredAllWarnings());

    return script;
  }
  public void testUnnamedFunctionStatement() {
    // Statements
    parseError("function() {};", "unnamed function statement");
    parseError("if (true) { function() {}; }", "unnamed function statement");
    parse("function f() {};");
    // Expressions
    parse("(function f() {});");
    parse("(function () {});");
  }
}
