/*
 * Copyright 2009 Google Inc.
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import java.util.List;
/**
 * Tests for {@link DevirtualizePrototypeMethods}
 *
*
 */
public class DevirtualizePrototypeMethodsTest extends CompilerTestCase {
  private final List<String> typeInformation;
  /**
   * Combine source strings using '\n' as the separator.
   */
  private static String newlineJoin(String ... parts) {
    return Joiner.on("\n").join(parts);
  }
  /**
   * Combine source strings using ';' as the separator.
   */
  private static String semicolonJoin(String ... parts) {
    return Joiner.on(";").join(parts);
  }
  /**
   * Verifies that the compiler pass's output matches the expected
   * output, and that nodes are annotated with the expected jstype
   * information.
   */
  private void checkTypes(String source,
                          String expected,
                          List<String> expectedTypes) {
    typeInformation.clear();
    test(source, expected);
    assertEquals(expectedTypes, typeInformation);
  }
  public void testRewritePrototypeMethods2() throws Exception {
    // type checking on
    enableTypeCheck(CheckLevel.ERROR);
    checkTypes(RewritePrototypeMethodTestInput.INPUT,
               RewritePrototypeMethodTestInput.EXPECTED,
               RewritePrototypeMethodTestInput.EXPECTED_TYPE_CHECKING_ON);
  }
}
