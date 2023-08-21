/*
 * Copyright 2011 The Closure Compiler Authors.
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
/**
 * Unit tests for {#link {@link PeepholeReplaceKnownMethods}
 *
 */
public class PeepholeReplaceKnownMethodsTest extends CompilerTestCase {
  private void foldSame(String js) {
    testSame(js);
  }
  private void fold(String js, String expected) {
    test(js, expected);
  }
  public void testStringJoinAdd() {
    fold("x = ['a', 'b', 'c'].join('')", "x = \"abc\"");
    fold("x = [].join(',')", "x = \"\"");
    fold("x = ['a'].join(',')", "x = \"a\"");
    fold("x = ['a', 'b', 'c'].join(',')", "x = \"a,b,c\"");
    fold("x = ['a', foo, 'b', 'c'].join(',')",
        "x = [\"a\",foo,\"b,c\"].join()");
    fold("x = [foo, 'a', 'b', 'c'].join(',')",
        "x = [foo,\"a,b,c\"].join()");
    fold("x = ['a', 'b', 'c', foo].join(',')",
        "x = [\"a,b,c\",foo].join()");

    // Works with numbers
    fold("x = ['a=', 5].join('')", "x = \"a=5\"");
    fold("x = ['a', '5'].join(7)", "x = \"a75\"");

    // Works on boolean
    fold("x = ['a=', false].join('')", "x = \"a=false\"");
    fold("x = ['a', '5'].join(true)", "x = \"atrue5\"");
    fold("x = ['a', '5'].join(false)", "x = \"afalse5\"");

    // Only optimize if it's a size win.
    fold("x = ['a', '5', 'c'].join('a very very very long chain')",
         "x = [\"a\",\"5\",\"c\"].join(\"a very very very long chain\")");

    // TODO(user): Its possible to fold this better.
    foldSame("x = ['', foo].join('-')");
    foldSame("x = ['', foo, ''].join()");

    fold("x = ['', '', foo, ''].join(',')",
         "x = [',', foo, ''].join()");
    fold("x = ['', '', foo, '', ''].join(',')",
         "x = [',', foo, ','].join()");

    fold("x = ['', '', foo, '', '', bar].join(',')",
         "x = [',', foo, ',', bar].join()");

    fold("x = [1,2,3].join('abcdef')",
         "x = '1abcdef2abcdef3'");

    fold("x = [1,2].join()", "x = '1,2'");
    fold("x = [null,undefined,''].join(',')", "x = ',,'");
    fold("x = [null,undefined,0].join(',')", "x = ',,0'");
    // This can be folded but we don't currently.
    foldSame("x = [[1,2],[3,4]].join()"); // would like: "x = '1,2,3,4'"
  }
}
