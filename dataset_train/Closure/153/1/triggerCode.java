/*
 * Copyright 2008 The Closure Compiler Authors.
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
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.Node;
import java.util.Set;
/**
 * @author johnlenz@google.com (John Lenz)
 *
 */
public class NormalizeTest extends CompilerTestCase {
  private static final String EXTERNS = "var window;";
  private String inFunction(String code) {
    return "(function(){" + code + "})";
  }
  private void testSameInFunction(String code) {
    testSame(inFunction(code));
  }
  private void testInFunction(String code, String expected) {
    test(inFunction(code), inFunction(expected));
  }
  private Set<Node> findNodesWithProperty(Node root, final int prop) {
    final Set<Node> set = Sets.newHashSet();
    NodeTraversal.traverse(
        getLastCompiler(), root, new AbstractPostOrderCallback() {
        public void visit(NodeTraversal t, Node node, Node parent) {
          if (node.getBooleanProp(prop)) {
            set.add(node);
          }
        }
      });
    return set;
  }
    private void testConstantProperties() {
      test("var a={}; a.ACONST = 4;var b = a.ACONST;",
           "var a$ACONST = 4; var b = a$ACONST;");

      test("var a={b:{}}; a.b.ACONST = 4;var b = a.b.ACONST;",
           "var a$b$ACONST = 4;var b = a$b$ACONST;");

      test("var a = {FOO: 1};var b = a.FOO;",
           "var a$FOO = 1; var b = a$FOO;");

      test("var EXTERN; var ext; ext.FOO;", "var b = EXTERN; var c = ext.FOO",
           "var b = EXTERN; var c = ext.FOO", null, null);

      test("var a={}; a.ACONST = 4; var b = a.ACONST;",
           "var a$ACONST = 4; var b = a$ACONST;");

      test("var a = {}; function foo() { var d = a.CONST; };" +
           "(function(){a.CONST=4})();",
           "var a$CONST;function foo(){var d = a$CONST;};" +
           "(function(){a$CONST = 4})();");

      test("var a = {}; a.ACONST = new Foo(); var b = a.ACONST;",
           "var a$ACONST = new Foo(); var b = a$ACONST;");
    }
  public void testDuplicateVarInExterns() {
    test("var extern;",
         "/** @suppress {duplicate} */ var extern = 3;", "var extern = 3;",
         null, null);
  }
}
