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
import com.google.javascript.jscomp.AbstractCompiler.LifeCycleStage;
import com.google.javascript.jscomp.FunctionInjector.CanInlineResult;
import com.google.javascript.jscomp.FunctionInjector.InliningMode;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.rhino.Node;
import junit.framework.TestCase;
import java.util.List;
import java.util.Set;
/**
 * Inline function tests.
 * @author johnlenz@google.com (John Lenz)
 */
public class FunctionInjectorTest extends TestCase {
  private boolean assumeStrictThis = false;
  private boolean assumeMinimumCapture = false;
  private FunctionInjector getInjector() {
    Compiler compiler = new Compiler();
    return new FunctionInjector(
        compiler, compiler.getUniqueNameIdSupplier(), true,
        assumeStrictThis, assumeMinimumCapture);
  }
  private void validateSourceInfo(Compiler compiler, Node subtree) {
    (new LineNumberCheck(compiler)).setCheckSubTree(subtree);
    // Source information problems are reported as compiler errors.
    if (compiler.getErrorCount() != 0) {
      String msg = "Error encountered: ";
      for (JSError err : compiler.getErrors()) {
        msg += err.toString() + "\n";
      }
      assertTrue(msg, compiler.getErrorCount() == 0);
    }
  }
  private static Node findFunction(Node n, String name) {
    if (n.isFunction()) {
      if (n.getFirstChild().getString().equals(name)) {
        return n;
      }
    }

    for (Node c : n.children()) {
      Node result = findFunction(c, name);
      if (result != null) {
        return result;
      }
    }

    return null;
  }
  private static Node prep(String js) {
    Compiler compiler = new Compiler();
    Node n = compiler.parseTestCode(js);
    assertEquals(0, compiler.getErrorCount());
    return n.getFirstChild();
  }
  private static Node parse(Compiler compiler, String js) {
    Node n = compiler.parseTestCode(js);
    assertEquals(0, compiler.getErrorCount());
    return n;
  }
  private static Node parseExpected(Compiler compiler, String js) {
    Node n = compiler.parseTestCode(js);
    String message = "Unexpected errors: ";
    JSError[] errs = compiler.getErrors();
    for (int i = 0; i < errs.length; i++){
      message += "\n" + errs[i].toString();
    }
    assertEquals(message, 0, compiler.getErrorCount());
    return n;
  }
  private static String toSource(Node n) {
    return new CodePrinter.Builder(n)
        .setPrettyPrint(false)
        .setLineBreak(false)
        .setSourceMap(null)
        .build();
  }
  public void testIssue1101a() {
    helperCanInlineReferenceToFunction(CanInlineResult.NO,
        "function foo(a){return modifiyX() + a;} foo(x);", "foo",
        INLINE_DIRECT);
  }
}
