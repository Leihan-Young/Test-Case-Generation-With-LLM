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
import com.google.javascript.rhino.Node;
import junit.framework.TestCase;
import java.io.IOException;
import java.util.List;
/**
 * Tests for {@link CommandLineRunner}.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
public class CommandLineRunnerTest extends TestCase {
  private Compiler lastCompiler = null;
  private CommandLineRunner lastCommandLineRunner = null;
  private boolean useStringComparison = false;
  private ModulePattern useModules = ModulePattern.NONE;
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
    test(original, compiled, null);
  }
  /**
   * Asserts that when compiling with the given compiler options,
   * {@code original} is transformed into {@code compiled}.
   * If {@code warning} is non-null, we will also check if the given
   * warning type was emitted.
   */
  private void test(String[] original, String[] compiled,
                    DiagnosticType warning) {
    Compiler compiler = compile(original);

    if (warning == null) {
      assertEquals("Expected no warnings or errors\n" +
          "Errors: \n" + Joiner.on("\n").join(compiler.getErrors()) +
          "Warnings: \n" + Joiner.on("\n").join(compiler.getWarnings()),
          0, compiler.getErrors().length + compiler.getWarnings().length);
    } else {
      assertEquals(1, compiler.getWarnings().length);
      assertEquals(warning, compiler.getWarnings()[0].getType());
    }

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
      assertEquals(1, compiler.getErrors().length);
      assertEquals(warning, compiler.getErrors()[0].getType());
    } else {
      assertEquals(1, compiler.getWarnings().length);
      assertEquals(warning, compiler.getWarnings()[0].getType());
    }
  }
  private Compiler compile(String[] original) {
    args.add("--js_output_file");
    args.add("/path/to/out.js");

    for (int i = 0; i < original.length; i++) {
      args.add("--js");
      args.add("/path/to/input" + i + ".js");
      if (useModules == ModulePattern.CHAIN) {
        args.add("--module");
        args.add("mod" + i + ":1" + (i > 0 ? (":mod" + (i - 1)) : ""));
      } else if (useModules == ModulePattern.STAR) {
        args.add("--module");
        args.add("mod" + i + ":1" + (i > 0 ? ":mod0" : ""));
      }
    }

    String[] argStrings = args.toArray(new String[] {});
    CommandLineRunner runner = new CommandLineRunner(argStrings);
    Compiler compiler = runner.createCompiler();
    lastCommandLineRunner = runner;
    lastCompiler = compiler;
    CompilerOptions options = runner.createOptions();
    try {
      runner.setRunOptions(options);
    } catch (AbstractCommandLineRunner.FlagUsageException e) {
      fail("Unexpected exception " + e);
    } catch (IOException e) {
      assert(false);
    }
    if (useModules != ModulePattern.NONE) {
      compiler.compile(
          externs,
          useModules == ModulePattern.STAR ?
              CompilerTestCase.createModuleStar(original) :
              CompilerTestCase.createModuleChain(original),
          options);
    } else {
      JSSourceFile[] inputs = new JSSourceFile[original.length];
      for (int i = 0; i < original.length; i++) {
        inputs[i] = JSSourceFile.fromCode("input" + i, original[i]);
      }
      compiler.compile(
          externs,
          inputs,
          options);
    }
    return compiler;
  }
  private Node parse(String[] original) {
    String[] argStrings = args.toArray(new String[] {});
    CommandLineRunner runner = new CommandLineRunner(argStrings);
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
  public void testCharSetExpansion() {
    testSame("");
    assertEquals("US-ASCII", lastCompiler.getOptions().outputCharset);
    args.add("--charset=UTF-8");
    testSame("");
    assertEquals("UTF-8", lastCompiler.getOptions().outputCharset);
  }
}
