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
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.flags.Flags;
import com.google.javascript.rhino.Node;
import junit.framework.TestCase;
import java.io.IOException;
/**
 * Tests for {@link CompilerRunner}.
 *
*
 */
public class CompilerRunnerTest extends TestCase {
  private Compiler lastCompiler = null;
  private boolean useStringComparison = false;
  /* Helper functions */

  private void testSame(String original) {
    testSame(new String[] { original });
  }
  private void testSame(String[] original) {
    test(original, original);
  }
  private void test(String original, String compiled) {
    test(new String[] { original }, new String[] { compiled });
  }
  /**
   * Asserts that when compiling with the given compiler options,
   * {@code original} is transformed into {@code compiled}.
   */
  private void test(String[] original, String[] compiled) {
    Compiler compiler = compile(original);
    assertEquals("Expected no warnings or errors\n" +
        "Errors: \n" + Joiner.on("\n").join(compiler.getErrors()) +
        "Warnings: \n" + Joiner.on("\n").join(compiler.getWarnings()),
        0, compiler.getErrors().length + compiler.getWarnings().length);

    Node root = compiler.getRoot().getLastChild();
    if (useStringComparison) {
      assertEquals(Joiner.on("").join(compiled), compiler.toSource());
    } else {
      Node expectedRoot = parse(compiled);
      String explanation = expectedRoot.checkTreeEquals(root);
      assertNull("\nExpected: " + compiler.toSource(expectedRoot) +
          "\nResult: " + compiler.toSource(root) +
          "\n" + explanation, explanation);
    }
  }
  /**
   * Asserts that when compiling, there is an error or warning.
   */
  private void test(String original, DiagnosticType warning) {
    test(new String[] { original }, warning);
  }
  /**
   * Asserts that when compiling, there is an error or warning.
   */
  private void test(String[] original, DiagnosticType warning) {
    Compiler compiler = compile(original);
    assertEquals("Expected exactly one warning or error " +
        "Errors: \n" + Joiner.on("\n").join(compiler.getErrors()) +
        "Warnings: \n" + Joiner.on("\n").join(compiler.getWarnings()),
        1, compiler.getErrors().length + compiler.getWarnings().length);
    if (compiler.getErrors().length > 0) {
      assertEquals(warning, compiler.getErrors()[0].getType());
    } else {
      assertEquals(warning, compiler.getWarnings()[0].getType());
    }
  }
  private Compiler compile(String original) {
    return compile( new String[] { original });
  }
  private Compiler compile(String[] original) {
    CompilerRunner runner = new CompilerRunner(new String[] {});
    Compiler compiler = runner.createCompiler();
    lastCompiler = compiler;
    JSSourceFile[] inputs = new JSSourceFile[original.length];
    for (int i = 0; i < original.length; i++) {
      inputs[i] = JSSourceFile.fromCode("input" + i, original[i]);
    }
    CompilerOptions options = runner.createOptions();
    try {
      runner.setRunOptions(options);
    } catch (AbstractCompilerRunner.FlagUsageException e) {
      fail("Unexpected exception " + e);
    } catch (IOException e) {
      assert(false);
    }
    compiler.compile(
        externs, CompilerTestCase.createModuleChain(original), options);
    return compiler;
  }
  private Node parse(String[] original) {
    CompilerRunner runner = new CompilerRunner(new String[] {});
    Compiler compiler = runner.createCompiler();
    JSSourceFile[] inputs = new JSSourceFile[original.length];
    for (int i = 0; i < inputs.length; i++) {
      inputs[i] = JSSourceFile.fromCode("input" + i, original[i]);
    }
    compiler.init(externs, inputs, new CompilerOptions());
    Node all = compiler.parseInputs();
    Node n = all.getLastChild();
    return n;
  }
  public void testIssue115() {
    CompilerRunner.FLAG_compilation_level.setForTest(
        CompilationLevel.SIMPLE_OPTIMIZATIONS);
    CompilerRunner.FLAG_warning_level.setForTest(
        WarningLevel.VERBOSE);
    test("function f() { " +
         "  var arguments = Array.prototype.slice.call(arguments, 0);" +
         "  return arguments[0]; " +
         "}",
         "function f() { " +
         "  arguments = Array.prototype.slice.call(arguments, 0);" +
         "  return arguments[0]; " +
         "}");
  }
}
