/*
 * Copyright 2009 The Closure Compiler Authors.
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
 * Tests for {@link FunctionRewriter}
 *
 */
public class FunctionRewriterTest extends CompilerTestCase {
  private void checkCompilesTo(String src,
                               String expectedHdr,
                               String expectedBody,
                               int repetitions) {
    StringBuilder srcBuffer = new StringBuilder();
    StringBuilder expectedBuffer = new StringBuilder();

    expectedBuffer.append(expectedHdr);

    for (int idx = 0; idx < repetitions; idx++) {
      if (idx != 0) {
        srcBuffer.append(";");
        expectedBuffer.append(";");
      }
      srcBuffer.append(src);
      expectedBuffer.append(expectedBody);
    }
    test(srcBuffer.toString(), expectedBuffer.toString());
  }
  private void checkCompilesToSame(String src, int repetitions) {
    checkCompilesTo(src, "", src, repetitions);
  }
  public void testIssue538() {
    checkCompilesToSame(      "/** @constructor */\n" +
        "WebInspector.Setting = function() {}\n" +
        "WebInspector.Setting.prototype = {\n" +
        "    get name0(){return this._name;},\n" +
        "    get name1(){return this._name;},\n" +
        "    get name2(){return this._name;},\n" +
        "    get name3(){return this._name;},\n" +
        "    get name4(){return this._name;},\n" +
        "    get name5(){return this._name;},\n" +
        "    get name6(){return this._name;},\n" +
        "    get name7(){return this._name;},\n" +
        "    get name8(){return this._name;},\n" +
        "    get name9(){return this._name;},\n" +
        "}", 1);
  }
}
