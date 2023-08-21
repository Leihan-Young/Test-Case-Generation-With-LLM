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
package org.apache.commons.csv;
import static org.apache.commons.csv.CSVFormat.RFC4180;
import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.LF;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.junit.Test;
/**
 *
 *
 * @version $Id$
 */
public class CSVFormatTest {
    private static void assertNotEquals(final Object right, final Object left) {
        assertFalse(right.equals(left));
        assertFalse(left.equals(right));
    }
    private static CSVFormat copy(final CSVFormat format) {
        return format.withDelimiter(format.getDelimiter());
    }
    @Test
    public void testNullRecordSeparatorCsv106() {
        final CSVFormat format = CSVFormat.newFormat(';').withSkipHeaderRecord(true).withHeader("H1", "H2");
        final String formatStr = format.format("A", "B");
        assertNotNull(formatStr);
        assertFalse(formatStr.endsWith("null"));
    }
}
