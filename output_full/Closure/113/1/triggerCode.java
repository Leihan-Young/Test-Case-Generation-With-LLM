/*
 * Copyright 2005 The Closure Compiler Authors.
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
import static com.google.javascript.jscomp.VarCheck.VAR_MULTIPLY_DECLARED_ERROR;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.Node;
public class VarCheckTest extends CompilerTestCase {
  private CheckLevel strictModuleDepErrorLevel;
  private boolean sanityCheck = false;
  private CheckLevel externValidationErrorLevel;
  private boolean declarationCheck;
  private void testDependentModules(String code1, String code2,
                                    DiagnosticType error) {
    testDependentModules(code1, code2, error, null);
  }
  private void testDependentModules(String code1, String code2,
                                    DiagnosticType error,
                                    DiagnosticType warning) {
    testTwoModules(code1, code2, true, error, warning);
  }
  private void testIndependentModules(String code1, String code2,
                                      DiagnosticType error,
                                      DiagnosticType warning) {
    testTwoModules(code1, code2, false, error, warning);
  }
  private void testTwoModules(String code1, String code2, boolean m2DependsOnm1,
                              DiagnosticType error, DiagnosticType warning) {
    JSModule m1 = new JSModule("m1");
    m1.add(SourceFile.fromCode("input1", code1));
    JSModule m2 = new JSModule("m2");
    m2.add(SourceFile.fromCode("input2", code2));
    if (m2DependsOnm1) {
      m2.addDependency(m1);
    }
    test(new JSModule[] { m1, m2 },
         new String[] { code1, code2 }, error, warning);
  }
  public void testNoUndeclaredVarWhenUsingClosurePass() {
    enableClosurePass();
    // We don't want to get goog as an undeclared var here.
    test("goog.require('namespace.Class1');\n", null,
        ProcessClosurePrimitives.MISSING_PROVIDE_ERROR);
  }
}
