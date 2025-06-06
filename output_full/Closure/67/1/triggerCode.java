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
/**
 * Tessts for {@link RemoveUnusedPrototypeProperties}.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
public class RemoveUnusedPrototypePropertiesTest extends CompilerTestCase {
  private boolean canRemoveExterns = false;
  private boolean anchorUnusedVars = false;
  public void testAliasing7() {
    // An exported alias must preserved any referenced values in the
    // referenced function.
    testSame("function e(){}" +
           "e.prototype['alias1'] = e.prototype.method1 = " +
               "function(){this.method2()};" +
           "e.prototype.method2 = function(){};");
  }
}
