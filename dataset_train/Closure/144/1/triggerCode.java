/*
 * Copyright 2004 Google Inc.
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
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;
public class CodePrinterTest extends TestCase {
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
    Node parse1 = parse(code);
    Node parse2 = parse(new CodePrinter.Builder(parse1).build());
    assertTrue(code, parse1.checkTreeEqualsSilent(parse2));
  }
  private void assertPrintNumber(String expected, double number) {
    assertPrint(String.valueOf(number), expected);
    assertPrintNode(expected, Node.newNumber(number));
  }
  private void assertPrintNumber(String expected, int number) {
    assertPrint(String.valueOf(number), expected);
    assertPrintNode(expected, Node.newNumber(number));
  }
  public void testTypeAnnotationsAssign() {
    assertTypeAnnotations("/** @constructor */ var Foo = function(){}",
        "/**\n * @return {undefined}\n * @constructor\n */\n"
        + "var Foo = function() {\n}");
  }
}
