/*
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Nick Santos
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.google.javascript.rhino.jstype;
import static com.google.javascript.rhino.jstype.TernaryValue.FALSE;
import static com.google.javascript.rhino.jstype.TernaryValue.TRUE;
import static com.google.javascript.rhino.jstype.TernaryValue.UNKNOWN;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfo.Visibility;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SimpleErrorReporter;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.ArrowType;
import com.google.javascript.rhino.jstype.JSType.TypePair;
import com.google.javascript.rhino.jstype.RecordTypeBuilder.RecordProperty;
import com.google.javascript.rhino.testing.Asserts;
import com.google.javascript.rhino.testing.BaseJSTypeTestCase;
import com.google.javascript.rhino.testing.AbstractStaticScope;
import com.google.javascript.rhino.testing.MapBasedScope;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class JSTypeTest extends BaseJSTypeTestCase {
  private FunctionType dateMethod;
  private FunctionType functionType;
  private NamedType unresolvedNamedType;
  private FunctionType googBar;
  private FunctionType googSubBar;
  private FunctionType googSubSubBar;
  private ObjectType googBarInst;
  private ObjectType googSubBarInst;
  private ObjectType googSubSubBarInst;
  private NamedType namedGoogBar;
  private ObjectType subclassOfUnresolvedNamedType;
  private FunctionType subclassCtor;
  private FunctionType interfaceType;
  private ObjectType interfaceInstType;
  private FunctionType subInterfaceType;
  private ObjectType subInterfaceInstType;
  private JSType recordType;
  private EnumType enumType;
  private EnumElementType elementsType;
  private NamedType forwardDeclaredNamedType;
  private List<JSType> types;
  private void assertPropertyTypeDeclared(ObjectType ownerType, String prop) {
    assertTrue(ownerType.isPropertyTypeDeclared(prop));
    assertFalse(ownerType.isPropertyTypeInferred(prop));
  }
  private void assertPropertyTypeInferred(ObjectType ownerType, String prop) {
    assertFalse(ownerType.isPropertyTypeDeclared(prop));
    assertTrue(ownerType.isPropertyTypeInferred(prop));
  }
  private void assertPropertyTypeUnknown(ObjectType ownerType, String prop) {
    assertFalse(ownerType.isPropertyTypeDeclared(prop));
    assertFalse(ownerType.isPropertyTypeInferred(prop));
    assertTrue(ownerType.getPropertyType(prop).isUnknownType());
  }
  private void assertReturnTypeEquals(JSType expectedReturnType,
      JSType function) {
    assertTrue(function instanceof FunctionType);
    assertTypeEquals(expectedReturnType,
        ((FunctionType) function).getReturnType());
  }
  private void compare(TernaryValue r, JSType t1, JSType t2) {
    assertEquals(r, t1.testForEquality(t2));
    assertEquals(r, t2.testForEquality(t1));
  }
  private void assertCanTestForEqualityWith(JSType t1, JSType t2) {
    assertTrue(t1.canTestForEqualityWith(t2));
    assertTrue(t2.canTestForEqualityWith(t1));
  }
  private void assertCannotTestForEqualityWith(JSType t1, JSType t2) {
    assertFalse(t1.canTestForEqualityWith(t2));
    assertFalse(t2.canTestForEqualityWith(t1));
  }
  /**
   * Types to test for symmetrical relationships.
   */
  private List<JSType> getTypesToTestForSymmetry() {
    return Lists.newArrayList(
        UNKNOWN_TYPE,
        NULL_TYPE,
        VOID_TYPE,
        NUMBER_TYPE,
        STRING_TYPE,
        BOOLEAN_TYPE,
        OBJECT_TYPE,
        U2U_CONSTRUCTOR_TYPE,
        LEAST_FUNCTION_TYPE,
        GREATEST_FUNCTION_TYPE,
        ALL_TYPE,
        NO_TYPE,
        NO_OBJECT_TYPE,
        NO_RESOLVED_TYPE,
        createUnionType(BOOLEAN_TYPE, STRING_TYPE),
        createUnionType(NUMBER_TYPE, STRING_TYPE),
        createUnionType(NULL_TYPE, dateMethod),
        createUnionType(UNKNOWN_TYPE, dateMethod),
        createUnionType(namedGoogBar, dateMethod),
        createUnionType(NULL_TYPE, unresolvedNamedType),
        enumType,
        elementsType,
        dateMethod,
        functionType,
        unresolvedNamedType,
        googBar,
        namedGoogBar,
        googBar.getInstanceType(),
        namedGoogBar,
        subclassOfUnresolvedNamedType,
        subclassCtor,
        recordType,
        forwardDeclaredNamedType,
        createUnionType(forwardDeclaredNamedType, NULL_TYPE));
  }
  private void testGetTypeUnderEquality(
      JSType t1, JSType t2, JSType t1Eq, JSType t2Eq) {
    // creating the pairs
    TypePair p12 = t1.getTypesUnderEquality(t2);
    TypePair p21 = t2.getTypesUnderEquality(t1);

    // t1Eq
    assertTypeEquals(t1Eq, p12.typeA);
    assertTypeEquals(t1Eq, p21.typeB);

    // t2Eq
    assertTypeEquals(t2Eq, p12.typeB);
    assertTypeEquals(t2Eq, p21.typeA);
  }
  private void testGetTypesUnderInequality(
      JSType t1, JSType t2, JSType t1Eq, JSType t2Eq) {
    // creating the pairs
    TypePair p12 = t1.getTypesUnderInequality(t2);
    TypePair p21 = t2.getTypesUnderInequality(t1);

    // t1Eq
    assertTypeEquals(t1Eq, p12.typeA);
    assertTypeEquals(t1Eq, p21.typeB);

    // t2Eq
    assertTypeEquals(t2Eq, p12.typeB);
    assertTypeEquals(t2Eq, p21.typeA);
  }
  /**
   * Assert that a type can assign to itself.
   */
  private void assertTypeCanAssignToItself(JSType type) {
    assertTrue(type.canAssignTo(type));
  }
  private static boolean containsType(
      Iterable<? extends JSType> types, JSType type) {
    for (JSType alt : types) {
      if (alt.isEquivalentTo(type)) {
        return true;
      }
    }
    return false;
  }
  private static boolean assertTypeListEquals(
      Iterable<? extends JSType> typeListA,
      Iterable<? extends JSType> typeListB) {
    for (JSType alt : typeListA) {
      assertTrue(
          "List : " + typeListA + "\n" +
          "does not contain: " + alt,
          containsType(typeListA, alt));
    }
    for (JSType alt : typeListB) {
      assertTrue(
          "List : " + typeListB + "\n" +
          "does not contain: " + alt,
          containsType(typeListB, alt));
    }
    return false;
  }
  private ArrowType createArrowType(Node params) {
    return registry.createArrowType(params);
  }
  public void testRecordTypeLeastSuperType2() {
    RecordTypeBuilder builder = new RecordTypeBuilder(registry);
    builder.addProperty("e", NUMBER_TYPE, null);
    builder.addProperty("b", STRING_TYPE, null);
    builder.addProperty("c", STRING_TYPE, null);
    JSType otherRecordType = builder.build();

    assertTypeEquals(
        registry.createUnionType(recordType, otherRecordType),
        recordType.getLeastSupertype(otherRecordType));
  }
}
