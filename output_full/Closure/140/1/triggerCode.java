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
/**
 * Tests for {@link CrossModuleCodeMotion}.
 *
*
 */
public class CrossModuleCodeMotionTest extends CompilerTestCase {
  private static final String EXTERNS = "alert";
  public void testEmptyModule() {
    // When the dest module is empty, it might try to move the code to the
    // one of the modules that the empty module depends on. In some cases
    // this might ended up to be the same module as the definition of the code.
    // When that happens, CrossMooduleCodeMotion might report a code change
    // while nothing is moved. This should not be a problem if we know all
    // modules are non-empty.
    JSModule m1 = new JSModule("m1");
    m1.add(JSSourceFile.fromCode("m1", "function x() {}"));
    
    JSModule empty = new JSModule("empty");
    empty.addDependency(m1);
    
    JSModule m2 = new JSModule("m2");
    m2.add(JSSourceFile.fromCode("m2", "x()"));
    m2.addDependency(empty);
    
    JSModule m3 = new JSModule("m3");
    m3.add(JSSourceFile.fromCode("m3", "x()"));
    m3.addDependency(empty);
    
    test(new JSModule[] {m1,empty,m2,m3},
        new String[] {
          "",
          "function x() {}",
          "x()",
          "x()"
    });
  }
}
