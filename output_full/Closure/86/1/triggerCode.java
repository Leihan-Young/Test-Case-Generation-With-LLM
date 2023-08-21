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
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.TernaryValue;
import junit.framework.TestCase;
import java.util.Collection;
import java.util.List;
import java.util.Set;
public class NodeUtilTest extends TestCase {
  private static Node parse(String js) {
    Compiler compiler = new Compiler();
    Node n = compiler.parseTestCode(js);
    assertEquals(0, compiler.getErrorCount());
    return n;
  }
  private void assertBooleanTrue(String val) {
    assertEquals(TernaryValue.TRUE, NodeUtil.getBooleanValue(getNode(val)));
  }
  private void assertBooleanFalse(String val) {
    assertEquals(TernaryValue.FALSE, NodeUtil.getBooleanValue(getNode(val)));
  }
  private void assertBooleanUnknown(String val) {
    assertEquals(TernaryValue.UNKNOWN, NodeUtil.getBooleanValue(getNode(val)));
  }
  private void assertExpressionBooleanTrue(String val) {
    assertEquals(TernaryValue.TRUE,
        NodeUtil.getExpressionBooleanValue(getNode(val)));
  }
  private void assertExpressionBooleanFalse(String val) {
    assertEquals(TernaryValue.FALSE,
        NodeUtil.getExpressionBooleanValue(getNode(val)));
  }
  private void assertExpressionBooleanUnknown(String val) {
    assertEquals(TernaryValue.UNKNOWN,
        NodeUtil.getExpressionBooleanValue(getNode(val)));
  }
  private Node parseExpr(String js) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.languageIn = LanguageMode.ECMASCRIPT5;
    compiler.initOptions(options);
    Node root = compiler.parseTestCode(js);
    return root.getFirstChild().getFirstChild();
  }
  private void testIsObjectLiteralKey(Node node, boolean expected) {
    assertEquals(expected, NodeUtil.isObjectLitKey(node, node.getParent()));
  }
  private void testGetFunctionName(Node function, String name) {
    assertEquals(Token.FUNCTION, function.getType());
    assertEquals(name, NodeUtil.getFunctionName(function));
  }
  private void assertSideEffect(boolean se, String js) {
    Node n = parse(js);
    assertEquals(se, NodeUtil.mayHaveSideEffects(n.getFirstChild()));
  }
  private void assertSideEffect(boolean se, String js, boolean GlobalRegExp) {
    Node n = parse(js);
    Compiler compiler = new Compiler();
    compiler.setHasRegExpGlobalReferences(GlobalRegExp);
    assertEquals(se, NodeUtil.mayHaveSideEffects(n.getFirstChild(), compiler));
  }
  private void assertMutableState(boolean se, String js) {
    Node n = parse(js);
    assertEquals(se, NodeUtil.mayEffectMutableState(n.getFirstChild()));
  }
  private void assertContainsAnonFunc(boolean expected, String js) {
    Node funcParent = findParentOfFuncDescendant(parse(js));
    assertNotNull("Expected function node in parse tree of: " + js, funcParent);
    Node funcNode = getFuncChild(funcParent);
    assertEquals(expected, NodeUtil.isFunctionExpression(funcNode));
  }
  private Node findParentOfFuncDescendant(Node n) {
    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
      if (c.getType() == Token.FUNCTION) {
        return n;
      }
      Node result = findParentOfFuncDescendant(c);
      if (result != null) {
        return result;
      }
    }
    return null;
  }
  private Node getFuncChild(Node n) {
    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
      if (c.getType() == Token.FUNCTION) {
        return c;
      }
    }
    return null;
  }
  private void assertNodeNames(Set<String> nodeNames, Collection<Node> nodes) {
    Set<String> actualNames = Sets.newHashSet();
    for (Node node : nodes) {
      actualNames.add(node.getString());
    }
    assertEquals(nodeNames, actualNames);
  }
  private boolean testLocalValue(String js) {
    Node script = parse("var test = " + js +";");
    Preconditions.checkState(script.getType() == Token.SCRIPT);
    Node var = script.getFirstChild();
    Preconditions.checkState(var.getType() == Token.VAR);
    Node name = var.getFirstChild();
    Preconditions.checkState(name.getType() == Token.NAME);
    Node value = name.getFirstChild();

    return NodeUtil.evaluatesToLocalValue(value);
  }
  private boolean testValidDefineValue(String js) {
    Node script = parse("var test = " + js +";");
    Node var = script.getFirstChild();
    Node name = var.getFirstChild();
    Node value = name.getFirstChild();

    ImmutableSet<String> defines = ImmutableSet.of();
    return NodeUtil.isValidDefineValue(value, defines);
  }
  public void testLocalValue1() throws Exception {
    // Names are not known to be local.
    assertFalse(testLocalValue("x"));
    assertFalse(testLocalValue("x()"));
    assertFalse(testLocalValue("this"));
    assertFalse(testLocalValue("arguments"));

    // We can't know if new objects are local unless we know
    // that they don't alias themselves.
    assertFalse(testLocalValue("new x()"));

    // property references are assume to be non-local
    assertFalse(testLocalValue("(new x()).y"));
    assertFalse(testLocalValue("(new x())['y']"));

    // Primitive values are local
    assertTrue(testLocalValue("null"));
    assertTrue(testLocalValue("undefined"));
    assertTrue(testLocalValue("Infinity"));
    assertTrue(testLocalValue("NaN"));
    assertTrue(testLocalValue("1"));
    assertTrue(testLocalValue("'a'"));
    assertTrue(testLocalValue("true"));
    assertTrue(testLocalValue("false"));
    assertTrue(testLocalValue("[]"));
    assertTrue(testLocalValue("{}"));

    // The contents of arrays and objects don't matter
    assertTrue(testLocalValue("[x]"));
    assertTrue(testLocalValue("{'a':x}"));

    // Pre-increment results in primitive number
    assertTrue(testLocalValue("++x"));
    assertTrue(testLocalValue("--x"));

    // Post-increment, the previous value matters.
    assertFalse(testLocalValue("x++"));
    assertFalse(testLocalValue("x--"));

    // The left side of an only assign matters if it is an alias or mutable.
    assertTrue(testLocalValue("x=1"));
    assertFalse(testLocalValue("x=[]"));
    assertFalse(testLocalValue("x=y"));
    // The right hand side of assignment opts don't matter, as they force
    // a local result.
    assertTrue(testLocalValue("x+=y"));
    assertTrue(testLocalValue("x*=y"));
    // Comparisons always result in locals, as they force a local boolean
    // result.
    assertTrue(testLocalValue("x==y"));
    assertTrue(testLocalValue("x!=y"));
    assertTrue(testLocalValue("x>y"));
    // Only the right side of a comma matters
    assertTrue(testLocalValue("(1,2)"));
    assertTrue(testLocalValue("(x,1)"));
    assertFalse(testLocalValue("(x,y)"));

    // Both the operands of OR matter
    assertTrue(testLocalValue("1||2"));
    assertFalse(testLocalValue("x||1"));
    assertFalse(testLocalValue("x||y"));
    assertFalse(testLocalValue("1||y"));

    // Both the operands of AND matter
    assertTrue(testLocalValue("1&&2"));
    assertFalse(testLocalValue("x&&1"));
    assertFalse(testLocalValue("x&&y"));
    assertFalse(testLocalValue("1&&y"));

    // Only the results of HOOK matter
    assertTrue(testLocalValue("x?1:2"));
    assertFalse(testLocalValue("x?x:2"));
    assertFalse(testLocalValue("x?1:x"));
    assertFalse(testLocalValue("x?x:y"));

    // Results of ops are local values
    assertTrue(testLocalValue("!y"));
    assertTrue(testLocalValue("~y"));
    assertTrue(testLocalValue("y + 1"));
    assertTrue(testLocalValue("y + z"));
    assertTrue(testLocalValue("y * z"));

    assertTrue(testLocalValue("'a' in x"));
    assertTrue(testLocalValue("typeof x"));
    assertTrue(testLocalValue("x instanceof y"));

    assertTrue(testLocalValue("void x"));
    assertTrue(testLocalValue("void 0"));

    assertFalse(testLocalValue("{}.x"));

    assertTrue(testLocalValue("{}.toString()"));
    assertTrue(testLocalValue("o.toString()"));

    assertFalse(testLocalValue("o.valueOf()"));
  }
}
