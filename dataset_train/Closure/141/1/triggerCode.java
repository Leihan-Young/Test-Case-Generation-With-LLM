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
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.ExpressionDecomposer.DecompositionType;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;
import java.util.Set;
import javax.annotation.Nullable;
/**
 * Unit tests for ExpressionDecomposer
 * @author johnlenz@google.com (John Lenz)
 */
public class ExpresssionDecomposerTest extends TestCase {
  /** Test case helpers. */

  private void helperCanExposeExpression(
      DecompositionType expectedResult,
      String code,
      String fnName
      ) {
    helperCanExposeExpression(expectedResult, code, fnName, null);
  }
  private void helperCanExposeAnonymousFunctionExpression(
      DecompositionType expectedResult, String code, int call) {
    Compiler compiler = new Compiler();
    Set<String> knownConstants = Sets.newHashSet();
    ExpressionDecomposer decomposer = new ExpressionDecomposer(
        compiler, compiler.getUniqueNameIdSupplier(), knownConstants);
    Node tree = parse(compiler, code);
    assertNotNull(tree);

    Node externsRoot = parse(compiler,
        "function goo() {}" +
        "function foo() {}");
    assertNotNull(externsRoot);
    Node mainRoot = tree;

    Node callSite = findCall(tree, null, 2);
    assertNotNull("Call " + call + " was not found.", callSite);

    compiler.resetUniqueNameId();
    DecompositionType result = decomposer.canExposeExpression(
        callSite);
    assertEquals(expectedResult, result);
  }
  private void helperCanExposeExpression(
      DecompositionType expectedResult,
      String code,
      String fnName,
      Set<String> knownConstants
      ) {
    Compiler compiler = new Compiler();
    if (knownConstants == null) {
      knownConstants = Sets.newHashSet();
    }
    ExpressionDecomposer decomposer = new ExpressionDecomposer(
        compiler, compiler.getUniqueNameIdSupplier(), knownConstants);
    Node tree = parse(compiler, code);
    assertNotNull(tree);

    Node externsRoot = parse(compiler,
        "function goo() {}" +
        "function foo() {}");
    assertNotNull(externsRoot);
    Node mainRoot = tree;

    Node callSite = findCall(tree, fnName);
    assertNotNull("Call to " + fnName + " was not found.", callSite);

    compiler.resetUniqueNameId();
    DecompositionType result = decomposer.canExposeExpression(
        callSite);
    assertEquals(expectedResult, result);
  }
  private void helperExposeExpression(
      String code,
      String fnName,
      String expectedResult
      ) {
    helperExposeExpression(
        code, fnName, expectedResult, null);
  }
  private void helperExposeExpression(
      String code,
      String fnName,
      String expectedResult,
      Set<String> knownConstants
      ) {
    Compiler compiler = new Compiler();
    if (knownConstants == null) {
      knownConstants = Sets.newHashSet();
    }
    ExpressionDecomposer decomposer = new ExpressionDecomposer(
        compiler, compiler.getUniqueNameIdSupplier(), knownConstants);
    decomposer.setTempNamePrefix("temp_");
    Node expectedRoot = parse(compiler, expectedResult);
    Node tree = parse(compiler, code);
    assertNotNull(tree);

    Node externsRoot = new Node(Token.EMPTY);
    Node mainRoot = tree;

    Node callSite = findCall(tree, fnName);
    assertNotNull("Call to " + fnName + " was not found.", callSite);

    DecompositionType result = decomposer.canExposeExpression(callSite);
    assertTrue(result == DecompositionType.DECOMPOSABLE);

    compiler.resetUniqueNameId();
    decomposer.exposeExpression(callSite);
    String explanation = expectedRoot.checkTreeEquals(tree);
    assertNull("\nExpected: " + compiler.toSource(expectedRoot) +
        "\nResult: " + compiler.toSource(tree) +
        "\n" + explanation, explanation);
  }
  private void helperMoveExpression(
      String code,
      String fnName,
      String expectedResult
      ) {
    helperMoveExpression(
        code, fnName, expectedResult, null);
  }
  private void helperMoveExpression(
      String code,
      String fnName,
      String expectedResult,
      Set<String> knownConstants
      ) {
    Compiler compiler = new Compiler();
    if (knownConstants == null) {
      knownConstants = Sets.newHashSet();
    }

    ExpressionDecomposer decomposer = new ExpressionDecomposer(
        compiler, compiler.getUniqueNameIdSupplier(), knownConstants);
    decomposer.setTempNamePrefix("temp_");
    Node expectedRoot = parse(compiler, expectedResult);
    Node tree = parse(compiler, code);
    assertNotNull(tree);

    Node externsRoot = new Node(Token.EMPTY);
    Node mainRoot = tree;

    Node callSite = findCall(tree, fnName);
    assertNotNull("Call to " + fnName + " was not found.", callSite);

    compiler.resetUniqueNameId();
    decomposer.moveExpression(callSite);
    String explanation = expectedRoot.checkTreeEquals(tree);
    assertNull("\nExpected: " + compiler.toSource(expectedRoot) +
        "\nResult: " + compiler.toSource(tree) +
        "\n" + explanation, explanation);
  }
  private static Node findCall(Node n, String name) {
    return findCall(n, name, 1);
  }
  /**
   * @param name The name to look for.
   * @param call The call to look for.
   * @return The return the Nth CALL node to name found in a pre-order 
   * traversal.
   */
  private static Node findCall(
      Node root, @Nullable final String name, final int call) {
    class Find {
      int found = 0;
      Node find(Node n) {
        if (n.getType() == Token.CALL) {
          Node callee = n.getFirstChild();
          if (name == null || (callee.getType() == Token.NAME
              && callee.getString().equals(name))) {
            found++;
            if (found == call) {
              return n;
            }
          }
        }

        for (Node c : n.children()) {
          Node result = find(c);
          if (result != null) {
            return result;
          }
        }

        return null;
      }
    }

    return (new Find()).find(root);
  }
  private static Node parse(Compiler compiler, String js) {
    Node n = compiler.parseTestCode(js);
    assertEquals(0, compiler.getErrorCount());
    return n;
  }
  public void testCanExposeExpression2() {
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "foo()", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "x = foo()", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "var x = foo()", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "if(foo()){}", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "switch(foo()){}", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "switch(foo()){}", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "function (){ return foo();}", "foo");

    helperCanExposeExpression(
        DecompositionType.MOVABLE, "x = foo() && 1", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "x = foo() || 1", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "x = foo() ? 0 : 1", "foo");
    helperCanExposeExpression(
        DecompositionType.MOVABLE, "(function(a){b = a})(foo())", "foo");
  }
}
