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
    compiler.initCompilerOptionsIfTesting();
    compiler.getOptions().languageIn = LanguageMode.ECMASCRIPT5;
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
    return NodeUtil.evaluatesToLocalValue(getNode(js));
  }
  private boolean testValidDefineValue(String js) {
    Node script = parse("var test = " + js +";");
    Node var = script.getFirstChild();
    Node name = var.getFirstChild();
    Node value = name.getFirstChild();

    ImmutableSet<String> defines = ImmutableSet.of();
    return NodeUtil.isValidDefineValue(value, defines);
  }
  public void testIsBooleanResult() {
    assertFalse(NodeUtil.isBooleanResult(getNode("1")));
    assertTrue(NodeUtil.isBooleanResult(getNode("true")));
    assertFalse(NodeUtil.isBooleanResult(getNode("+true")));
    assertFalse(NodeUtil.isBooleanResult(getNode("+1")));
    assertFalse(NodeUtil.isBooleanResult(getNode("-1")));
    assertFalse(NodeUtil.isBooleanResult(getNode("-Infinity")));
    assertFalse(NodeUtil.isBooleanResult(getNode("Infinity")));
    assertFalse(NodeUtil.isBooleanResult(getNode("NaN")));
    assertFalse(NodeUtil.isBooleanResult(getNode("undefined")));
    assertFalse(NodeUtil.isBooleanResult(getNode("void 0")));

    assertFalse(NodeUtil.isBooleanResult(getNode("a << b")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a >> b")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a >>> b")));

    assertTrue(NodeUtil.isBooleanResult(getNode("a == b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a != b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a === b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a !== b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a < b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a > b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a <= b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a >= b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a in b")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a instanceof b")));

    assertFalse(NodeUtil.isBooleanResult(getNode("'a'")));
    assertFalse(NodeUtil.isBooleanResult(getNode("'a'+b")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a+'b'")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a+b")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a()")));
    assertFalse(NodeUtil.isBooleanResult(getNode("''.a")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a.b")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a.b()")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a().b()")));
    assertFalse(NodeUtil.isBooleanResult(getNode("new a()")));
    assertTrue(NodeUtil.isBooleanResult(getNode("delete a")));

    // Definitely not boolean
    assertFalse(NodeUtil.isBooleanResult(getNode("([true,false])")));
    assertFalse(NodeUtil.isBooleanResult(getNode("({a:true})")));

    // These are boolean but aren't handled yet, "false" here means "unknown".
    assertTrue(NodeUtil.isBooleanResult(getNode("true && false")));
    assertTrue(NodeUtil.isBooleanResult(getNode("true || false")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a ? true : false")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a,true")));
    assertTrue(NodeUtil.isBooleanResult(getNode("a=true")));
    assertFalse(NodeUtil.isBooleanResult(getNode("a=1")));
  }
}
