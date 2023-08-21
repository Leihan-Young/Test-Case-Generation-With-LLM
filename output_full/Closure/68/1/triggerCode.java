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
package com.google.javascript.jscomp.parsing;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.mozilla.rhino.CompilerEnvirons;
import com.google.javascript.jscomp.mozilla.rhino.Parser;
import com.google.javascript.jscomp.mozilla.rhino.Token.CommentType;
import com.google.javascript.jscomp.mozilla.rhino.ast.AstRoot;
import com.google.javascript.jscomp.mozilla.rhino.ast.Comment;
import com.google.javascript.jscomp.parsing.Config.LanguageMode;
import com.google.javascript.jscomp.testing.TestErrorReporter;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfo.Visibility;
import com.google.javascript.rhino.JSTypeExpression;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.ObjectType;
import com.google.javascript.rhino.testing.BaseJSTypeTestCase;
import java.util.Collection;
import java.util.List;
import java.util.Set;
public class JsDocInfoParserTest extends BaseJSTypeTestCase {
  private Set<String> extraAnnotations;
  private Set<String> extraSuppressions;
  private Node.FileLevelJsDocBuilder fileLevelJsDocBuilder = null;
  private void testParseType(String type) throws Exception {
    testParseType(type, type);
  }
  private void testParseType(
      String type, String typeExpected) throws Exception {
    JSDocInfo info = parse("@type {" + type + "}*/");

    assertNotNull(info);
    assertTrue(info.hasType());
    assertEquals(typeExpected, resolve(info.getType()).toString());
  }
  /**
   * Asserts that a documentation field exists on the given marker.
   *
   * @param description The text of the documentation field expected.
   * @param startCharno The starting character of the text.
   * @param endLineno The ending line of the text.
   * @param endCharno The ending character of the text.
   * @return The marker, for chaining purposes.
   */
  private JSDocInfo.Marker assertDocumentationInMarker(JSDocInfo.Marker marker,
                                                       String description,
                                                       int startCharno,
                                                       int endLineno,
                                                       int endCharno) {
    assertTrue(marker.description != null);
    assertEquals(description, marker.description.getItem());

    // Match positional information.
    assertEquals(marker.annotation.getStartLine(),
                 marker.description.getStartLine());
    assertEquals(startCharno, marker.description.getPositionOnStartLine());
    assertEquals(endLineno, marker.description.getEndLine());
    assertEquals(endCharno, marker.description.getPositionOnEndLine());

    return marker;
  }
  /**
   * Asserts that a type field exists on the given marker.
   *
   * @param typeName The name of the type expected in the type field.
   * @param startCharno The starting character of the type declaration.
   * @param hasBrackets Whether the type in the type field is expected
   *     to have brackets.
   * @return The marker, for chaining purposes.
   */
  private JSDocInfo.Marker assertTypeInMarker(JSDocInfo.Marker marker,
                                            String typeName, int startCharno,
                                            boolean hasBrackets) {

    assertTrue(marker.type != null);
    assertTrue(marker.type.getItem().getType() == Token.STRING);

    // Match the name and brackets information.
    String foundName = marker.type.getItem().getString();

    assertEquals(typeName, foundName);
    assertEquals(hasBrackets, marker.type.hasBrackets);

    // Match position information.
    assertEquals(startCharno, marker.type.getPositionOnStartLine());

    int endCharno = startCharno + foundName.length();

    if (hasBrackets) {
      endCharno += 1;
    }

    assertEquals(endCharno, marker.type.getPositionOnEndLine());
    assertEquals(marker.annotation.getStartLine(), marker.type.getStartLine());
    assertEquals(marker.annotation.getStartLine(), marker.type.getEndLine());

    return marker;
  }
  /**
   * Asserts that a name field exists on the given marker.
   *
   * @param name The name expected in the name field.
   * @param startCharno The starting character of the text.
   * @return The marker, for chaining purposes.
   */
  private JSDocInfo.Marker assertNameInMarker(JSDocInfo.Marker marker,
                                            String name, int startCharno) {
    assertTrue(marker.name != null);
    assertEquals(name, marker.name.getItem());

    assertEquals(startCharno, marker.name.getPositionOnStartLine());
    assertEquals(startCharno + name.length(),
                 marker.name.getPositionOnEndLine());

    assertEquals(marker.annotation.getStartLine(), marker.name.getStartLine());
    assertEquals(marker.annotation.getStartLine(), marker.name.getEndLine());

    return marker;
  }
  /**
   * Asserts that an annotation marker of a given annotation name
   * is found in the given JSDocInfo.
   *
   * @param jsdoc The JSDocInfo in which to search for the annotation marker.
   * @param annotationName The name/type of the annotation for which to
   *   search. Example: "author" for an "@author" annotation.
   * @param startLineno The expected starting line number of the marker.
   * @param startCharno The expected character on the starting line.
   * @return The marker found, for further testing.
   */
  private JSDocInfo.Marker assertAnnotationMarker(JSDocInfo jsdoc,
                                                  String annotationName,
                                                  int startLineno,
                                                  int startCharno) {
    return assertAnnotationMarker(jsdoc, annotationName, startLineno,
                                  startCharno, 0);
  }
  /**
   * Asserts that the index-th annotation marker of a given annotation name
   * is found in the given JSDocInfo.
   *
   * @param jsdoc The JSDocInfo in which to search for the annotation marker.
   * @param annotationName The name/type of the annotation for which to
   *   search. Example: "author" for an "@author" annotation.
   * @param startLineno The expected starting line number of the marker.
   * @param startCharno The expected character on the starting line.
   * @param index The index of the marker.
   * @return The marker found, for further testing.
   */
  private JSDocInfo.Marker assertAnnotationMarker(JSDocInfo jsdoc,
                                                  String annotationName,
                                                  int startLineno,
                                                  int startCharno,
                                                  int index) {

    Collection<JSDocInfo.Marker> markers = jsdoc.getMarkers();

    assertTrue(markers.size() > 0);

    int counter = 0;

    for (JSDocInfo.Marker marker : markers) {
      if (marker.annotation != null) {
        if (annotationName.equals(marker.annotation.getItem())) {

          if (counter == index) {
            assertEquals(startLineno, marker.annotation.getStartLine());
            assertEquals(startCharno,
                         marker.annotation.getPositionOnStartLine());
            assertEquals(startLineno, marker.annotation.getEndLine());
            assertEquals(startCharno + annotationName.length(),
                         marker.annotation.getPositionOnEndLine());

            return marker;
          }

          counter++;
        }
      }
    }

    fail("No marker found");
    return null;
  }
  private <T> void assertContains(Collection<T> collection, T item) {
    assertTrue(collection.contains(item));
  }
  private void parseFull(String code, String... warnings) {
    CompilerEnvirons environment = new CompilerEnvirons();

    TestErrorReporter testErrorReporter = new TestErrorReporter(null, warnings);
    environment.setErrorReporter(testErrorReporter);

    environment.setRecordingComments(true);
    environment.setRecordingLocalJsDocComments(true);

    Parser p = new Parser(environment, testErrorReporter);
    AstRoot script = p.parse(code, null, 0);

    Config config =
        new Config(extraAnnotations, extraSuppressions,
            true, LanguageMode.ECMASCRIPT3, false);
    for (Comment comment : script.getComments()) {
      JsDocInfoParser jsdocParser =
        new JsDocInfoParser(
            new JsDocTokenStream(comment.getValue().substring(3),
                comment.getLineno()),
            comment,
            script.getSourceName(),
            config,
            testErrorReporter);
      jsdocParser.parse();
      jsdocParser.retrieveAndResetParsedJSDocInfo();
    }

    assertTrue("some expected warnings were not reported",
        testErrorReporter.hasEncounteredAllWarnings());
  }
  private JSDocInfo parseFileOverviewWithoutDoc(String comment,
                                                String... warnings) {
    return parse(comment, false, true, warnings);
  }
  private JSDocInfo parseFileOverview(String comment, String... warnings) {
    return parse(comment, true, true, warnings);
  }
  private JSDocInfo parse(String comment, String... warnings) {
    return parse(comment, false, warnings);
  }
  private JSDocInfo parse(String comment, boolean parseDocumentation,
                          String... warnings) {
    return parse(comment, parseDocumentation, false, warnings);
  }
  private JSDocInfo parse(String comment, boolean parseDocumentation,
      boolean parseFileOverview, String... warnings) {
    TestErrorReporter errorReporter = new TestErrorReporter(null, warnings);

    Config config = new Config(extraAnnotations, extraSuppressions,
        parseDocumentation, LanguageMode.ECMASCRIPT3, false);
    JsDocInfoParser jsdocParser = new JsDocInfoParser(
        stream(comment),
        new Comment(0, 0, CommentType.JSDOC, comment),
        "testcode", config, errorReporter);

    if (fileLevelJsDocBuilder != null) {
      jsdocParser.setFileLevelJsDocBuilder(fileLevelJsDocBuilder);
    }

    jsdocParser.parse();

    assertTrue("expected warnings were not reported",
        errorReporter.hasEncounteredAllWarnings());

    if (parseFileOverview) {
      return jsdocParser.getFileOverviewJSDocInfo();
    } else {
      return jsdocParser.retrieveAndResetParsedJSDocInfo();
    }
  }
  private Node parseType(String typeComment) {
    return JsDocInfoParser.parseTypeString(typeComment);
  }
  private JsDocTokenStream stream(String source) {
    return new JsDocTokenStream(source, 0);
  }
  private void assertParameterTypeEquals(JSType expected, JSTypeExpression te) {
    assertEquals(expected, ((ObjectType) resolve(te)).getParameterType());
  }
  private void assertIndexTypeEquals(JSType expected, JSTypeExpression te) {
    assertEquals(expected, ((ObjectType) resolve(te)).getIndexType());
  }
  public void testIssue477() throws Exception {
    parse("@type function */",
        "Bad type annotation. missing opening (");
  }
}
