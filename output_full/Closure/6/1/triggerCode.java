/*
 * Copyright 2006 The Closure Compiler Authors.
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
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.Scope.Var;
import com.google.javascript.jscomp.type.ClosureReverseAbstractInterpreter;
import com.google.javascript.jscomp.type.SemanticReverseAbstractInterpreter;
import com.google.javascript.rhino.InputId;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeNative;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.testing.Asserts;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
/**
 * Tests {@link TypeCheck}.
 *
 * This is a temporary fork of the TypeCheckTest for the experimental
 * "looseTypes" option.  These tests should be be folded into TypeCheckTest
 * or removed along with the looseTypes option.
 *
 */
public class LooseTypeCheckTest extends CompilerTypeTestCase {
  /**
   * Tests the type of a function definition. The function defined by
   * {@code functionDef} should be named {@code "f"}.
   */
  private void testFunctionType(String functionDef, String functionType)
      throws Exception {
    testFunctionType(functionDef, "f", functionType);
  }
  /**
   * Tests the type of a function definition. The function defined by
   * {@code functionDef} should be named {@code functionName}.
   */
  private void testFunctionType(String functionDef, String functionName,
      String functionType) throws Exception {
    // using the variable initialization check to verify the function's type
    testTypes(
        functionDef +
        "/** @type number */var a=" + functionName + ";",
        "initializing variable\n" +
        "found   : " + functionType + "\n" +
        "required: number");
  }
  /**
   * Tests the type of a function definition in externs.
   * The function defined by {@code functionDef} should be
   * named {@code functionName}.
   */
  private void testExternFunctionType(String functionDef, String functionName,
      String functionType) throws Exception {
    testTypes(
        functionDef,
        "/** @type number */var a=" + functionName + ";",
        "initializing variable\n" +
        "found   : " + functionType + "\n" +
        "required: number", false);
  }
  /**
   * Type checks a NAME node and retrieve its type.
   */
  private JSType testNameNode(String name) {
    Node node = Node.newString(Token.NAME, name);
    Node parent = new Node(Token.SCRIPT, node);
    parent.setInputId(new InputId("code"));

    Node externs = new Node(Token.SCRIPT);
    externs.setInputId(new InputId("externs"));

    Node externAndJsRoot = new Node(Token.BLOCK, externs, parent);
    externAndJsRoot.setIsSyntheticBlock(true);

    makeTypeCheck().processForTesting(null, parent);
    return node.getJSType();
  }
  private void testAddingMethodsUsingPrototypeIdiomComplexNamespace(
      TypeCheckResult p) {
    ObjectType goog = (ObjectType) p.scope.getVar("goog").getType();
    assertEquals(NATIVE_PROPERTIES_COUNT + 1, goog.getPropertiesCount());
    JSType googA = goog.getPropertyType("A");
    assertNotNull(googA);
    assertTrue(googA instanceof FunctionType);
    FunctionType googAFunction = (FunctionType) googA;
    ObjectType classA = googAFunction.getInstanceType();
    assertEquals(NATIVE_PROPERTIES_COUNT + 1, classA.getPropertiesCount());
    checkObjectType(classA, "m1", NUMBER_TYPE);
  }
  private double getTypedPercent(String js) throws Exception {
    Node n = compiler.parseTestCode(js);

    Node externs = new Node(Token.BLOCK);
    Node externAndJsRoot = new Node(Token.BLOCK, externs, n);
    externAndJsRoot.setIsSyntheticBlock(true);

    TypeCheck t = makeTypeCheck();
    t.processForTesting(null, n);
    return t.getTypedPercent();
  }
  private ObjectType getInstanceType(Node js1Node) {
    JSType type = js1Node.getFirstChild().getJSType();
    assertNotNull(type);
    assertTrue(type instanceof FunctionType);
    FunctionType functionType = (FunctionType) type;
    assertTrue(functionType.isConstructor());
    return functionType.getInstanceType();
  }
  private void checkObjectType(ObjectType objectType, String propertyName,
        JSType expectedType) {
    assertTrue("Expected " + objectType.getReferenceName() +
        " to have property " +
        propertyName, objectType.hasProperty(propertyName));
    assertTypeEquals("Expected " + objectType.getReferenceName() +
        "'s property " +
        propertyName + " to have type " + expectedType,
        expectedType, objectType.getPropertyType(propertyName));
  }
  private void testTypes(String js) throws Exception {
    testTypes(js, (String) null);
  }
  private void testTypes(String js, String description) throws Exception {
    testTypes(js, description, false);
  }
  private void testTypes(String js, DiagnosticType type) throws Exception {
    testTypes(js, type.format(), false);
  }
  private void testClosureTypes(String js, String description)
      throws Exception {
    testClosureTypesMultipleWarnings(js,
        description == null ? null : Lists.newArrayList(description));
  }
  private void testClosureTypesMultipleWarnings(
      String js, List<String> descriptions) throws Exception {
    Node n = compiler.parseTestCode(js);
    Node externs = new Node(Token.BLOCK);
    Node externAndJsRoot = new Node(Token.BLOCK, externs, n);
    externAndJsRoot.setIsSyntheticBlock(true);

    assertEquals("parsing error: " +
        Joiner.on(", ").join(compiler.getErrors()),
        0, compiler.getErrorCount());

    // For processing goog.addDependency for forward typedefs.
    new ProcessClosurePrimitives(compiler, null, CheckLevel.ERROR)
        .process(null, n);

    CodingConvention convention = compiler.getCodingConvention();
    new TypeCheck(compiler,
        new ClosureReverseAbstractInterpreter(
            convention, registry).append(
                new SemanticReverseAbstractInterpreter(
                    convention, registry))
            .getFirst(),
        registry)
        .processForTesting(null, n);

    assertEquals(0, compiler.getErrorCount());

    if (descriptions == null) {
      assertEquals(
          "unexpected warning(s) : " +
          Joiner.on(", ").join(compiler.getWarnings()),
          0, compiler.getWarningCount());
    } else {
      assertEquals(descriptions.size(), compiler.getWarningCount());
      Set<String> actualWarningDescriptions = Sets.newHashSet();
      for (int i = 0; i < descriptions.size(); i++) {
        actualWarningDescriptions.add(compiler.getWarnings()[i].description);
      }
      assertEquals(
          Sets.newHashSet(descriptions), actualWarningDescriptions);
    }
  }
  /**
   * Parses and type checks the JavaScript code.
   */
  private Node parseAndTypeCheck(String js) {
    return parseAndTypeCheck(DEFAULT_EXTERNS, js);
  }
  private Node parseAndTypeCheck(String externs, String js) {
    return parseAndTypeCheckWithScope(externs, js).root;
  }
  /**
   * Parses and type checks the JavaScript code and returns the Scope used
   * whilst type checking.
   */
  private TypeCheckResult parseAndTypeCheckWithScope(String js) {
    return parseAndTypeCheckWithScope(DEFAULT_EXTERNS, js);
  }
  private TypeCheckResult parseAndTypeCheckWithScope(
      String externs, String js) {
    compiler.init(
        Lists.newArrayList(SourceFile.fromCode("[externs]", externs)),
        Lists.newArrayList(SourceFile.fromCode("[testcode]", js)),
        compiler.getOptions());

    Node n = compiler.getInput(new InputId("[testcode]")).getAstRoot(compiler);
    Node externsNode = compiler.getInput(new InputId("[externs]"))
        .getAstRoot(compiler);
    Node externAndJsRoot = new Node(Token.BLOCK, externsNode, n);
    externAndJsRoot.setIsSyntheticBlock(true);

    assertEquals("parsing error: " +
        Joiner.on(", ").join(compiler.getErrors()),
        0, compiler.getErrorCount());

    Scope s = makeTypeCheck().processForTesting(externsNode, n);
    return new TypeCheckResult(n, s);
  }
  private Node typeCheck(Node n) {
    Node externsNode = new Node(Token.BLOCK);
    Node externAndJsRoot = new Node(Token.BLOCK, externsNode, n);
    externAndJsRoot.setIsSyntheticBlock(true);

    makeTypeCheck().processForTesting(null, n);
    return n;
  }
  private TypeCheck makeTypeCheck() {
    return new TypeCheck(
        compiler,
        new SemanticReverseAbstractInterpreter(
            compiler.getCodingConvention(), registry),
        registry);
  }
  public void testTypeRedefinition() throws Exception {
    testClosureTypesMultipleWarnings(
        "a={};/**@enum {string}*/ a.A = {ZOR:'b'};"
        + "/** @constructor */ a.A = function() {}",
        Lists.newArrayList(
            "variable a.A redefined with type function (new:a.A): undefined, " +
            "original definition at [testcode]:1 with type enum{a.A}",
            "assignment to property A of a\n" +
            "found   : function (new:a.A): undefined\n" +
            "required: enum{a.A}"));
  }
}
