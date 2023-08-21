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
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.rhino.InputId;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;
public class CodePrinterTest extends TestCase {
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
    parse(expected); // validate the expected string is valid js
    assertEquals(expected,
        parsePrint(js, false, CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD));
  }
  private void assertLineBreak(String js, String expected) {
    assertEquals(expected,
        parsePrint(js, false, true,
            CodePrinter.DEFAULT_LINE_LENGTH_THRESHOLD));
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
  public void testNumericKeys() {
    assertPrint("var x = {010: 1};", "var x={8:1}");
    assertPrint("var x = {'010': 1};", "var x={\"010\":1}");

    assertPrint("var x = {0x10: 1};", "var x={16:1}");
    assertPrint("var x = {'0x10': 1};", "var x={\"0x10\":1}");

    // I was surprised at this result too.
    assertPrint("var x = {.2: 1};", "var x={\"0.2\":1}");
    assertPrint("var x = {'.2': 1};", "var x={\".2\":1}");

    assertPrint("var x = {0.2: 1};", "var x={\"0.2\":1}");
    assertPrint("var x = {'0.2': 1};", "var x={\"0.2\":1}");
  }
}
