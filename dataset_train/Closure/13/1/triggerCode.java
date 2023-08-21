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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
/**
 * Tests for {@link PassFactory}.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
public class IntegrationTest extends IntegrationTestCase {
  public void testIssue787() {
    CompilerOptions options = createCompilerOptions();
    CompilationLevel level = CompilationLevel.SIMPLE_OPTIMIZATIONS;
    level.setOptionsForCompilationLevel(options);
    WarningLevel warnings = WarningLevel.DEFAULT;
    warnings.setOptionsForWarningLevel(options);

    String code = "" +
        "function some_function() {\n" +
        "  var fn1;\n" +
        "  var fn2;\n" +
        "\n" +
        "  if (any_expression) {\n" +
        "    fn2 = external_ref;\n" +
        "    fn1 = function (content) {\n" +
        "      return fn2();\n" +
        "    }\n" +
        "  }\n" +
        "\n" +
        "  return {\n" +
        "    method1: function () {\n" +
        "      if (fn1) fn1();\n" +
        "      return true;\n" +
        "    },\n" +
        "    method2: function () {\n" +
        "      return false;\n" +
        "    }\n" +
        "  }\n" +
        "}";

    String result = "" +
        "function some_function() {\n" +
        "  var a, b;\n" +
        "  any_expression && (b = external_ref, a = function() {\n" +
        "    return b()\n" +
        "  });\n" +
        "  return{method1:function() {\n" +
        "    a && a();\n" +
        "    return !0\n" +
        "  }, method2:function() {\n" +
        "    return !1\n" +
        "  }}\n" +
        "}\n" +
        "";

    test(options, code, result);
  }
}
