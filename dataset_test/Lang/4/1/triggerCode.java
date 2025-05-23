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
package org.apache.commons.lang3.text.translate;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
/**
 * Unit tests for {@link org.apache.commons.lang3.text.translate.LookupTranslator}.
 * @version $Id$
 */
public class LookupTranslatorTest  {
    @Test
    public void testLang882() throws IOException {
        final LookupTranslator lt = new LookupTranslator(new CharSequence[][] { { new StringBuffer("one"), new StringBuffer("two") } });
        final StringWriter out = new StringWriter();
        final int result = lt.translate(new StringBuffer("one"), 0, out);
        assertEquals("Incorrect codepoint consumption", 3, result);
        assertEquals("Incorrect value", "two", out.toString());
    }
}
