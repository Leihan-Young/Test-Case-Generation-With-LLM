/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.fraction;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import junit.framework.TestCase;
public class FractionFormatTest extends TestCase {
    public void testParseProperInvalidMinus() {
        String source = "2 -2 / 3";
        try {
            Fraction c = properFormat.parse(source);
            fail("invalid minus in improper fraction.");
        } catch (ParseException ex) {
            // expected
        }
        source = "2 2 / -3";
        try {
            Fraction c = properFormat.parse(source);
            fail("invalid minus in improper fraction.");
        } catch (ParseException ex) {
            // expected
        }
    }
}
