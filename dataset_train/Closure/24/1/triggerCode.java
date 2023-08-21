/*
 * Copyright 2010 The Closure Compiler Authors.
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformation;
import com.google.javascript.jscomp.CompilerOptions.AliasTransformationHandler;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SourcePosition;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * Tests for {@link ScopedAliases}
 *
 * @author robbyw@google.com (Robby Walker)
 */
public class ScopedAliasesTest extends CompilerTestCase {
  private void testScoped(String code, String expected) {
    test(GOOG_SCOPE_START_BLOCK + code + GOOG_SCOPE_END_BLOCK, expected);
  }
  private void testScopedNoChanges(String aliases, String code) {
    testScoped(aliases + code, code);
  }
  private void testTypes(String aliases, String code) {
    testScopedNoChanges(aliases, code);
    verifyTypes();
  }
  private void verifyTypes() {
    Compiler lastCompiler = getLastCompiler();
    new TypeVerifyingPass(lastCompiler).process(lastCompiler.externsRoot,
        lastCompiler.jsRoot);
  }
  private void testFailure(String code, DiagnosticType expectedError) {
    test(code, null, expectedError);
  }
  private void testScopedFailure(String code, DiagnosticType expectedError) {
    test("goog.scope(function() {" + code + "});", null, expectedError);
  }
  private void verifyAliasTransformationPosition(int startLine, int startChar,
      int endLine, int endChar, SourcePosition<AliasTransformation> pos) {
    assertEquals(startLine, pos.getStartLine());
    assertEquals(startChar, pos.getPositionOnStartLine());
    assertTrue(
        "expected endline >= " + endLine + ".  Found " + pos.getEndLine(),
        pos.getEndLine() >= endLine);
    assertTrue("expected endChar >= " + endChar + ".  Found "
        + pos.getPositionOnEndLine(), pos.getPositionOnEndLine() >= endChar);
  }
  public void testNonAliasLocal() {
    testScopedFailure("var x = 10", ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
    testScopedFailure("var x = goog.dom + 10",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
    testScopedFailure("var x = goog['dom']",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
    testScopedFailure("var x = goog.dom, y = 10",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
    testScopedFailure("function f() {}",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }
}
