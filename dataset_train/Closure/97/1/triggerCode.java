/*
 * Copyright 2004 Google Inc.
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
 * Tests for PeepholeFoldConstants in isolation. Tests for the interaction of
 * multiple peephole passes are in PeepholeIntegrationTest.
 */
public class PeepholeFoldConstantsTest extends CompilerTestCase {
  private void foldSame(String js) {
    testSame(js);
  }
  private void fold(String js, String expected) {
    test(js, expected);
  }
  private void fold(String js, String expected, DiagnosticType warning) {
    test(js, expected, warning);
  }
  private void assertResultString(String js, String expected) {
    PeepholeFoldConstantsTest scTest = new PeepholeFoldConstantsTest(false);

    scTest.test(js, expected);
  }
  public void testFoldBitShifts() {
    fold("x = 1 << 0", "x = 1");
    fold("x = -1 << 0", "x = -1");
    fold("x = 1 << 1", "x = 2");
    fold("x = 3 << 1", "x = 6");
    fold("x = 1 << 8", "x = 256");

    fold("x = 1 >> 0", "x = 1");
    fold("x = -1 >> 0", "x = -1");
    fold("x = 1 >> 1", "x = 0");
    fold("x = 2 >> 1", "x = 1");
    fold("x = 5 >> 1", "x = 2");
    fold("x = 127 >> 3", "x = 15");
    fold("x = 3 >> 1", "x = 1");
    fold("x = 3 >> 2", "x = 0");
    fold("x = 10 >> 1", "x = 5");
    fold("x = 10 >> 2", "x = 2");
    fold("x = 10 >> 5", "x = 0");

    fold("x = 10 >>> 1", "x = 5");
    fold("x = 10 >>> 2", "x = 2");
    fold("x = 10 >>> 5", "x = 0");
    fold("x = -1 >>> 1", "x = 2147483647"); // 0x7fffffff
    fold("x = -1 >>> 0", "x = 4294967295"); // 0xffffffff
    fold("x = -2 >>> 0", "x = 4294967294"); // 0xfffffffe

    fold("3000000000 << 1", "3000000000<<1",
         PeepholeFoldConstants.BITWISE_OPERAND_OUT_OF_RANGE);
    fold("1 << 32", "1<<32",
        PeepholeFoldConstants.SHIFT_AMOUNT_OUT_OF_BOUNDS);
    fold("1 << -1", "1<<32",
        PeepholeFoldConstants.SHIFT_AMOUNT_OUT_OF_BOUNDS);
    fold("3000000000 >> 1", "3000000000>>1",
        PeepholeFoldConstants.BITWISE_OPERAND_OUT_OF_RANGE);
    fold("1 >> 32", "1>>32",
        PeepholeFoldConstants.SHIFT_AMOUNT_OUT_OF_BOUNDS);
    fold("1.5 << 0",  "1.5<<0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
    fold("1 << .5",   "1.5<<0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
    fold("1.5 >>> 0", "1.5>>>0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
    fold("1 >>> .5",  "1.5>>>0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
    fold("1.5 >> 0",  "1.5>>0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
    fold("1 >> .5",   "1.5>>0",
        PeepholeFoldConstants.FRACTIONAL_BITWISE_OPERAND);
  }
}
