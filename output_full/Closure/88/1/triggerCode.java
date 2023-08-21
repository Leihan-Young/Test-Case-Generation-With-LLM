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
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.javascript.rhino.Node;
import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
/**
 * Tests for {@link CommandLineRunner}.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
public class CommandLineRunnerTest extends TestCase {
  private Compiler lastCompiler = null;
  private CommandLineRunner lastCommandLineRunner = null;
  private List<Integer> exitCodes = null;
  private ByteArrayOutputStream outReader = null;
  private ByteArrayOutputStream errReader = null;
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

    assertTrue(exitCodes.size() > 0);
    int lastExitCode = exitCodes.get(exitCodes.size() - 1);

    if (compiler.getErrors().length > 0) {
      assertEquals(1, compiler.getErrors().length);
      assertEquals(warning, compiler.getErrors()[0].getType());
      assertEquals(1, lastExitCode);
    } else {
      assertEquals(1, compiler.getWarnings().length);
      assertEquals(warning, compiler.getWarnings()[0].getType());
      assertEquals(0, lastExitCode);
    }
  }
  private CommandLineRunner createCommandLineRunner(String[] original) {
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
    return new CommandLineRunner(
        argStrings,
        new PrintStream(outReader),
        new PrintStream(errReader));
  }
  private Compiler compile(String[] original) {
    CommandLineRunner runner = createCommandLineRunner(original);
    assertTrue(runner.shouldRunCompiler());
    Supplier<List<JSSourceFile>> inputsSupplier = null;
    Supplier<List<JSModule>> modulesSupplier = null;

    if (useModules == ModulePattern.NONE) {
      List<JSSourceFile> inputs = Lists.newArrayList();
      for (int i = 0; i < original.length; i++) {
        inputs.add(JSSourceFile.fromCode("input" + i, original[i]));
      }
      inputsSupplier = Suppliers.ofInstance(inputs);
    } else if (useModules == ModulePattern.STAR) {
      modulesSupplier = Suppliers.<List<JSModule>>ofInstance(
          Lists.<JSModule>newArrayList(
              CompilerTestCase.createModuleStar(original)));
    } else if (useModules == ModulePattern.CHAIN) {
      modulesSupplier = Suppliers.<List<JSModule>>ofInstance(
          Lists.<JSModule>newArrayList(
              CompilerTestCase.createModuleChain(original)));
    } else {
      throw new IllegalArgumentException("Unknown module type: " + useModules);
    }

    runner.enableTestMode(
        Suppliers.<List<JSSourceFile>>ofInstance(externs),
        inputsSupplier,
        modulesSupplier,
        new Function<Integer, Boolean>() {
          @Override
          public Boolean apply(Integer code) {
            return exitCodes.add(code);
          }
        });
    runner.run();
    lastCompiler = runner.getCompiler();
    lastCommandLineRunner = runner;
    return lastCompiler;
  }
  private Node parse(String[] original) {
    String[] argStrings = args.toArray(new String[] {});
    CommandLineRunner runner = new CommandLineRunner(argStrings);
    Compiler compiler = runner.createCompiler();
    List<JSSourceFile> inputs = Lists.newArrayList();
    for (int i = 0; i < original.length; i++) {
      inputs.add(JSSourceFile.fromCode("input" + i, original[i]));
    }
    compiler.init(externs, inputs, new CompilerOptions());
    Node all = compiler.parseInputs();
    Node n = all.getLastChild();
    return n;
  }
  public void testIssue297() {
    args.add("--compilation_level=SIMPLE_OPTIMIZATIONS");
    test("function f(p) {" +
         " var x;" +
         " return ((x=p.id) && (x=parseInt(x.substr(1))) && x>0);" +
         "}",
         "function f(b) {" +
         " var a;" +
         " return ((a=b.id) && (a=parseInt(a.substr(1))) && a>0);" +
         "}");
  }  
}
