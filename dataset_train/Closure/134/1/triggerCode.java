/*
 * Copyright 2008 Google Inc.
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
import com.google.common.collect.Maps;
import com.google.javascript.rhino.Node;
import java.util.Map;
/**
 * Unit test for AmbiguateProperties Compiler pass.
 *
*
 */
public class AmbiguatePropertiesTest extends CompilerTestCase {
  private AmbiguateProperties lastPass;
  public void testImplementsAndExtends() {
    String js = ""
        + "/** @interface */ function Foo() {}\n"
        + "/**\n"
        + " * @constructor\n"
        + " */\n"
        + "function Bar(){}\n"
        + "Bar.prototype.y = function() { return 3; };\n"
        + "/**\n"
        + " * @constructor\n"
        + " * @extends {Bar}\n"
        + " * @implements {Foo}\n"
        + " */\n"
        + "function SubBar(){ }\n"
        + "/** @param {Foo} x */ function f(x) { x.z = 3; }\n"
        + "/** @param {SubBar} x */ function g(x) { x.z = 3; }";
    String output = ""
        + "function Foo(){}\n"
        + "function Bar(){}\n"
        + "Bar.prototype.b = function() { return 3; };\n"
        + "function SubBar(){}\n"
        + "function f(x) { x.a = 3; }\n"
        + "function g(x) { x.a = 3; }";
    test(js, output);
  }
}
