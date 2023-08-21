/*
 * Copyright 2011 The Closure Compiler Authors.
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
 * Verifies that valid candidates for object literals are inlined as
 * expected, and invalid candidates are not touched.
 *
 */
public class InlineObjectLiteralsTest extends CompilerTestCase {
  private void testLocal(String code, String result) {
    test(LOCAL_PREFIX + code + LOCAL_POSTFIX,
         LOCAL_PREFIX + result + LOCAL_POSTFIX);
  }
  private void testSameLocal(String code) {
    testLocal(code, code);
  }
  public void testObject10() {
    testLocal("var x; var b = f(); x = {a:a, b:b}; if(x.a) g(x.b);",
         "var JSCompiler_object_inline_a_0;" +
         "var JSCompiler_object_inline_b_1;" +
         "var b = f();" +
         "JSCompiler_object_inline_a_0=a,JSCompiler_object_inline_b_1=b,true;" +
         "if(JSCompiler_object_inline_a_0) g(JSCompiler_object_inline_b_1)");
    testLocal("var x = {}; var b = f(); x = {a:a, b:b}; if(x.a) g(x.b) + x.c",
         "var x = {}; var b = f(); x = {a:a, b:b}; if(x.a) g(x.b) + x.c");
    testLocal("var x; var b = f(); x = {a:a, b:b}; x.c = c; if(x.a) g(x.b) + x.c",
         "var JSCompiler_object_inline_a_0;" +
         "var JSCompiler_object_inline_b_1;" +
         "var JSCompiler_object_inline_c_2;" +
         "var b = f();" +
         "JSCompiler_object_inline_a_0 = a,JSCompiler_object_inline_b_1 = b, " +
         "  JSCompiler_object_inline_c_2=void 0,true;" +
         "JSCompiler_object_inline_c_2 = c;" +
         "if (JSCompiler_object_inline_a_0)" +
         "  g(JSCompiler_object_inline_b_1) + JSCompiler_object_inline_c_2;");
    testLocal("var x = {a:a}; if (b) x={b:b}; f(x.a||x.b);",
         "var JSCompiler_object_inline_a_0 = a;" +
         "var JSCompiler_object_inline_b_1;" +
         "if(b) JSCompiler_object_inline_b_1 = b," +
         "      JSCompiler_object_inline_a_0 = void 0," +
         "      true;" +
         "f(JSCompiler_object_inline_a_0 || JSCompiler_object_inline_b_1)");
    testLocal("var x; var y = 5; x = {a:a, b:b, c:c}; if (b) x={b:b}; f(x.a||x.b);",
         "var JSCompiler_object_inline_a_0;" +
         "var JSCompiler_object_inline_b_1;" +
         "var JSCompiler_object_inline_c_2;" +
         "var y=5;" +
         "JSCompiler_object_inline_a_0=a," +
         "JSCompiler_object_inline_b_1=b," +
         "JSCompiler_object_inline_c_2=c," +
         "true;" +
         "if (b) JSCompiler_object_inline_b_1=b," +
         "       JSCompiler_object_inline_a_0=void 0," +
         "       JSCompiler_object_inline_c_2=void 0," +
         "       true;" +
         "f(JSCompiler_object_inline_a_0||JSCompiler_object_inline_b_1)");
  }
}
