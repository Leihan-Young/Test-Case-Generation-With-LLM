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
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.rhino.InputId;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;
import java.util.List;
public class CodePrinterTest extends TestCase {
  private boolean trustedStrings = true;
  private Compiler lastCompiler = null;
  private LanguageMode languageMode = LanguageMode.ECMASCRIPT5;
  private static void checkUnexpectedErrorsOrWarnings(
      Compiler compiler, int expected) {
    int actual = compiler.getErrors().length + compiler.getWarnings().length;
    if (actual != expected) {
      String msg = "";
      for (JSError err : compiler.getErrors()) {
        msg += "Error:" + err.toString() + "\n";
      }
      for (JSError err : compiler.getWarnings()) {
        msg += "Warning:" + err.toString() + "\n";
      }
      assertEquals("Unexpected warnings or errors.\n " + msg, expected, actual);
    }
  }
  private void assertPrint(String js, String expected) {
    parse(expected); // validate the expected string is valid JS
    assertEquals(expected,
        parsePrint(js, false, CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD));
  }
  private void assertPrintSame(String js) {
    assertPrint(js, js);
  }
  private void assertLineBreak(String js, String expected) {
    assertEquals(expected,
        parsePrint(js, false, true,
            CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD));
  }
  private void assertLineBreakAtEndOfFile(String js,
      String expectedWithoutBreakAtEnd, String expectedWithBreakAtEnd) {
    assertEquals(expectedWithoutBreakAtEnd,
        parsePrint(js, false, false, false, 30));
    assertEquals(expectedWithBreakAtEnd,
        parsePrint(js, false, false, true, 30));
  }
  private void assertPrettyPrint(String js, String expected) {
    assertEquals(expected,
        parsePrint(js, true, false,
            CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD));
  }
  private void assertTypeAnnotations(String js, String expected) {
    assertEquals(expected,
        parsePrint(js, true, false,
            CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD, true));
  }
  private void assertLineLength(String js, String expected) {
    assertEquals(expected,
        parsePrint(js, false, true, 10));
  }
  private void testReparse(String code) {
    Compiler compiler = new Compiler();
    Node parse1 = parse(code);
    Node parse2 = parse(new CodePrinter.Builder(parse1).build());
    String explanation = parse1.checkTreeEquals(parse2);
    assertNull("\nExpected: " + compiler.toSource(parse1) +
        "\nResult: " + compiler.toSource(parse2) +
        "\n" + explanation, explanation);
  }
  private void assertPrintNumber(String expected, double number) {
    assertPrint(String.valueOf(number), expected);
    assertPrintNode(expected, Node.newNumber(number));
  }
  private void assertPrintNumber(String expected, int number) {
    assertPrint(String.valueOf(number), expected);
    assertPrintNode(expected, Node.newNumber(number));
  }
  public void testPrintInOperatorInForLoop() {
    // Check for in expression in for's init expression.
    // Check alone, with + (higher precedence), with ?: (lower precedence),
    // and with conditional.
    assertPrint("var a={}; for (var i = (\"length\" in a); i;) {}",
        "var a={};for(var i=(\"length\"in a);i;);");
    assertPrint("var a={}; for (var i = (\"length\" in a) ? 0 : 1; i;) {}",
        "var a={};for(var i=(\"length\"in a)?0:1;i;);");
    assertPrint("var a={}; for (var i = (\"length\" in a) + 1; i;) {}",
        "var a={};for(var i=(\"length\"in a)+1;i;);");
    assertPrint("var a={};for (var i = (\"length\" in a|| \"size\" in a);;);",
        "var a={};for(var i=(\"length\"in a)||(\"size\"in a);;);");
    assertPrint("var a={};for (var i = a || a || (\"size\" in a);;);",
        "var a={};for(var i=a||a||(\"size\"in a);;);");

    // Test works with unary operators and calls.
    assertPrint("var a={}; for (var i = -(\"length\" in a); i;) {}",
        "var a={};for(var i=-(\"length\"in a);i;);");
    assertPrint("var a={};function b_(p){ return p;};" +
        "for(var i=1,j=b_(\"length\" in a);;) {}",
        "var a={};function b_(p){return p}" +
            "for(var i=1,j=b_(\"length\"in a);;);");

    // Test we correctly handle an in operator in the test clause.
    assertPrint("var a={}; for (;(\"length\" in a);) {}",
        "var a={};for(;\"length\"in a;);");

    // Test we correctly handle an in operator inside a comma.
    assertPrintSame("for(x,(y in z);;)foo()");
    assertPrintSame("for(var x,w=(y in z);;)foo()");

    // And in operator inside a hook.
    assertPrintSame("for(a=c?0:(0 in d);;)foo()");
  }
}
