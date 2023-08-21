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
import static org.apache.commons.csv.Constants.BACKSPACE;
import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.FF;
import static org.apache.commons.csv.Constants.LF;
import static org.apache.commons.csv.Constants.TAB;
import static org.apache.commons.csv.Token.Type.COMMENT;
import static org.apache.commons.csv.Token.Type.EOF;
import static org.apache.commons.csv.Token.Type.EORECORD;
import static org.apache.commons.csv.Token.Type.TOKEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.apache.commons.csv.TokenMatchers.hasContent;
import static org.apache.commons.csv.TokenMatchers.matches;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Before;
import org.junit.Test;
/**
 *
 *
 * @version $Id$
 */
public class CSVLexerTest {
    private CSVFormat formatWithEscaping;
    private Lexer getLexer(final String input, final CSVFormat format) {
        return new CSVLexer(format, new ExtendedBufferedReader(new StringReader(input)));
    }
    @Test
    public void testEscapedMySqlNullValue() throws Exception {
        // MySQL uses \N to symbolize null values. We have to restore this
        final Lexer lexer = getLexer("character\\NEscaped", formatWithEscaping);
        assertThat(lexer.nextToken(new Token()), hasContent("character\\NEscaped"));
    }
}
