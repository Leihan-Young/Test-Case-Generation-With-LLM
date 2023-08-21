/*
 * Copyright 2006 Google Inc.
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
import com.google.javascript.rhino.Node;
import com.google.javascript.jscomp.CheckLevel;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.BASE_CLASS_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.DUPLICATE_NAMESPACE_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.FUNCTION_NAMESPACE_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.INVALID_ARGUMENT_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.INVALID_PROVIDE_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.LATE_PROVIDE_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.MISSING_PROVIDE_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.NON_STRING_PASSED_TO_SET_CSS_NAME_MAPPING_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.NULL_ARGUMENT_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.TOO_MANY_ARGUMENTS_ERROR;
import static com.google.javascript.jscomp.ProcessClosurePrimitives.XMODULE_REQUIRE_ERROR;
/**
 * Tests for {@link ProcessClosurePrimitives}.
 *
 */
public class ProcessClosurePrimitivesTest extends CompilerTestCase {
  private String additionalCode;
  private String additionalEndCode;
  private boolean addAdditionalNamespace;
  public void testProvideInIndependentModules4() {
    // Regression test for bug 261:
    // http://code.google.com/p/closure-compiler/issues/detail?id=261
    test(
        createModuleStar(
            "goog.provide('apps');",
            "goog.provide('apps.foo.bar.B');",
            "goog.provide('apps.foo.bar.C');"),
        new String[] {
            "var apps = {};apps.foo = {};apps.foo.bar = {}",
            "apps.foo.bar.B = {};",
            "apps.foo.bar.C = {};",
        });
  }
}
