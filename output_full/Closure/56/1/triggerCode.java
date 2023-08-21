/*
 * Copyright 2007 The Closure Compiler Authors.
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
import junit.framework.TestCase;
/**
 */
public class JSCompilerSourceExcerptProviderTest extends TestCase {
  private SourceExcerptProvider provider;
  /**
   * Asserts that a region is 'well formed': it must not be an empty and
   * cannot start or finish by a carriage return. In addition, it must
   * contain the line whose region we are taking.
   */
  private void assertRegionWellFormed(String sourceName, int lineNumber) {
    Region region = provider.getSourceRegion(sourceName, lineNumber);
    assertNotNull(region);
    String sourceRegion = region.getSourceExcerpt();
    assertNotNull(sourceRegion);
    if (lineNumber == 1) {
      assertEquals(1, region.getBeginningLineNumber());
    } else {
      assertTrue(region.getBeginningLineNumber() <= lineNumber);
    }
    assertTrue(lineNumber <= region.getEndingLineNumber());
    assertNotSame(sourceRegion, 0, sourceRegion.length());
    assertNotSame(sourceRegion, '\n', sourceRegion.charAt(0));
    assertNotSame(sourceRegion,
        '\n', sourceRegion.charAt(sourceRegion.length() - 1));
    String line = provider.getSourceLine(sourceName, lineNumber);
    assertTrue(sourceRegion, sourceRegion.contains(line));
  }
  public void testExceptNoNewLine() throws Exception {
    assertEquals("foo2:first line", provider.getSourceLine("foo2", 1));
    assertEquals("foo2:second line", provider.getSourceLine("foo2", 2));
    assertEquals("foo2:third line", provider.getSourceLine("foo2", 3));
    assertEquals(null, provider.getSourceLine("foo2", 4));
  }
}
