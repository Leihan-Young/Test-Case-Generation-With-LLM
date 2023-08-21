/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang;
import java.io.StringWriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * Unit tests for {@link StringEscapeUtils}.
 *
 * @author of original StringUtilsTest.testEscape = ?
 * @author <a href="mailto:alex@purpletech.com">Alexander Day Chaffee</a>
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id$
 */
public class EntitiesTest extends TestCase
{
    private void doTestEscapeNamedEntity(final String expected, final String entity) throws Exception
    {
        assertEquals(expected, entities.escape(entity));
        StringWriter writer = new StringWriter();
        entities.escape(writer, entity);
        assertEquals(expected, writer.toString());
    }
    private void doTestUnescapeEntity(final String expected, final String entity) throws Exception
    {
        assertEquals(expected, entities.unescape(entity));
        StringWriter writer = new StringWriter();
        entities.unescape(writer, entity);
        assertEquals(expected, writer.toString());
    }
    private void checkSomeEntityMap(Entities.EntityMap map) {
        map.add("foo", 1);
        assertEquals(1, map.value("foo"));
        assertEquals("foo", map.name(1));
        map.add("bar", 2);
        map.add("baz", 3);
        assertEquals(3, map.value("baz"));
        assertEquals("baz", map.name(3));
    }
    public void testNumberOverflow() throws Exception {
        doTestUnescapeEntity("&#12345678;", "&#12345678;");
        doTestUnescapeEntity("x&#12345678;y", "x&#12345678;y");
        doTestUnescapeEntity("&#x12345678;", "&#x12345678;");
        doTestUnescapeEntity("x&#x12345678;y", "x&#x12345678;y");
    }
}
