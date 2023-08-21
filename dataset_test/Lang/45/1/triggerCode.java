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
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Unit tests for WordUtils class.
 * 
 * @author <a href="mailto:ridesmet@users.sourceforge.net">Ringo De Smet</a>
 * @author Stephen Colebourne
 * @version $Id$
 */
public class WordUtilsTest extends TestCase {
    public void testAbbreviate() {
        // check null and empty are returned respectively
        assertNull(WordUtils.abbreviate(null, 1,-1,""));
        assertEquals(StringUtils.EMPTY, WordUtils.abbreviate("", 1,-1,""));

        // test upper limit
        assertEquals("01234", WordUtils.abbreviate("0123456789", 0,5,""));
        assertEquals("01234", WordUtils.abbreviate("0123456789", 5, 2,""));
        assertEquals("012", WordUtils.abbreviate("012 3456789", 2, 5,""));
        assertEquals("012 3", WordUtils.abbreviate("012 3456789", 5, 2,""));
        assertEquals("0123456789", WordUtils.abbreviate("0123456789", 0,-1,""));

        // test upper limit + append string
        assertEquals("01234-", WordUtils.abbreviate("0123456789", 0,5,"-"));
        assertEquals("01234-", WordUtils.abbreviate("0123456789", 5, 2,"-"));
        assertEquals("012", WordUtils.abbreviate("012 3456789", 2, 5, null));
        assertEquals("012 3", WordUtils.abbreviate("012 3456789", 5, 2,""));
        assertEquals("0123456789", WordUtils.abbreviate("0123456789", 0,-1,""));

        // test lower value
        assertEquals("012", WordUtils.abbreviate("012 3456789", 0,5, null));
        assertEquals("01234", WordUtils.abbreviate("01234 56789", 5, 10, null));
        assertEquals("01 23 45 67", WordUtils.abbreviate("01 23 45 67 89", 9, -1, null));
        assertEquals("01 23 45 6", WordUtils.abbreviate("01 23 45 67 89", 9, 10, null));
        assertEquals("0123456789", WordUtils.abbreviate("0123456789", 15, 20, null));

        // test lower value + append
        assertEquals("012", WordUtils.abbreviate("012 3456789", 0,5, null));
        assertEquals("01234-", WordUtils.abbreviate("01234 56789", 5, 10, "-"));
        assertEquals("01 23 45 67abc", WordUtils.abbreviate("01 23 45 67 89", 9, -1, "abc"));
        assertEquals("01 23 45 6", WordUtils.abbreviate("01 23 45 67 89", 9, 10, ""));

        // others
        assertEquals("", WordUtils.abbreviate("0123456790", 0,0,""));
        assertEquals("", WordUtils.abbreviate(" 0123456790", 0,-1,""));
    }
}
