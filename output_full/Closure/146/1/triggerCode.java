/*
 * Copyright 2007 Google Inc.
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
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import java.util.Arrays;
import java.util.Collection;
public class SemanticReverseAbstractInterpreterTest
    extends CompilerTypeTestCase {
  private CodingConvention codingConvention = new GoogleCodingConvention();
  private ReverseAbstractInterpreter interpreter;
  private Scope functionScope;
  private void testBinop(FlowScope blind, int binop, Node left, Node right,
      Collection<TypedName> trueOutcome,
      Collection<TypedName> falseOutcome) {
    Node condition = new Node(binop);
    condition.addChildToBack(left);
    condition.addChildToBack(right);

    // true outcome.
    FlowScope informedTrue = interpreter.
        getPreciserScopeKnowingConditionOutcome(condition, blind, true);
    for (TypedName p : trueOutcome) {
      assertEquals(p.name, p.type, getVarType(informedTrue, p.name));
    }

    // false outcome.
    FlowScope informedFalse = interpreter.
        getPreciserScopeKnowingConditionOutcome(condition, blind, false);
    for (TypedName p : falseOutcome) {
      assertEquals(p.type, getVarType(informedFalse, p.name));
    }
  }
  private Node createNull() {
    Node n = new Node(Token.NULL);
    n.setJSType(NULL_TYPE);
    return n;
  }
  private Node createNumber(int n) {
    Node number = createUntypedNumber(n);
    number.setJSType(NUMBER_TYPE);
    return number;
  }
  private Node createUntypedNumber(int n) {
    return Node.newNumber(n);
  }
  private JSType getVarType(FlowScope scope, String name) {
    return scope.getSlot(name).getType();
  }
  private Node createVar(FlowScope scope, String name, JSType type) {
    Node n = Node.newString(Token.NAME, name);
    functionScope.declare(name, n, null, null);
    ((LinkedFlowScope) scope).inferSlotType(name, type);
    n.setJSType(type);
    return n;
  }
  /**
   * Tests reverse interpretation of two undefineds.
   */
  @SuppressWarnings("unchecked")
  public void testEqCondition4() throws Exception {
    FlowScope blind = newScope();
    testBinop(blind,
        Token.EQ,
        createVar(blind, "a", VOID_TYPE),
        createVar(blind, "b", VOID_TYPE),
        Sets.newHashSet(
            new TypedName("a", VOID_TYPE),
            new TypedName("b", VOID_TYPE)),
        Sets.newHashSet(
            new TypedName("a", NO_TYPE),
            new TypedName("b", NO_TYPE)));
  }
}
