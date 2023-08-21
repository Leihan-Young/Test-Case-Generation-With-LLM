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
package com.google.debugging.sourcemap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.javascript.jscomp.SourceMap;
import com.google.javascript.jscomp.SourceMap.Format;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * @author johnlenz@google.com (John Lenz)
 */
public class SourceMapGeneratorV3Test extends SourceMapTestCase {
  private String getEmptyMapFor(String name) throws IOException {
    StringWriter out = new StringWriter();
    SourceMapGeneratorV3 generator = new SourceMapGeneratorV3();
    generator.appendTo(out, name);
    return out.toString();
  }
  public void testParseSourceMetaMap() throws Exception {
    final String INPUT1 = "file1";
    final String INPUT2 = "file2";
    LinkedHashMap<String, String> inputs = Maps.newLinkedHashMap();
    inputs.put(INPUT1, "var __FOO__ = 1;");
    inputs.put(INPUT2, "var __BAR__ = 2;");
    RunResult result1 = compile(inputs.get(INPUT1), INPUT1);
    RunResult result2 = compile(inputs.get(INPUT2), INPUT2);

    final String MAP1 = "map1";
    final String MAP2 = "map2";
    final LinkedHashMap<String, String> maps = Maps.newLinkedHashMap();
    maps.put(MAP1, result1.sourceMapFileContent);
    maps.put(MAP2, result2.sourceMapFileContent);

    List<SourceMapSection> sections = Lists.newArrayList();

    StringBuilder output = new StringBuilder();
    FilePosition offset = appendAndCount(output, result1.generatedSource);
    sections.add(SourceMapSection.forURL(MAP1, 0, 0));
    output.append(result2.generatedSource);
    sections.add(
        SourceMapSection.forURL(MAP2, offset.getLine(), offset.getColumn()));

    SourceMapGeneratorV3 generator = new SourceMapGeneratorV3();
    StringBuilder mapContents = new StringBuilder();
    generator.appendIndexMapTo(mapContents, "out.js", sections);

    check(inputs, output.toString(), mapContents.toString(),
      new SourceMapSupplier() {
        @Override
        public String getSourceMap(String url){
          return maps.get(url);
      }});
  }
}
