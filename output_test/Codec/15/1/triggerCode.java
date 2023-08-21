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
package org.apache.commons.codec.language;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoderAbstractTest;
import org.junit.Assert;
import org.junit.Test;
/**
 * Tests {@link Soundex}.
 *
 * <p>Keep this file in UTF-8 encoding for proper Javadoc processing.</p>
 *
 * @version $Id$
 */
public class SoundexTest extends StringEncoderAbstractTest<Soundex> {
    /**
     * Consonants from the same code group separated by W or H are treated as one.
     */
    @Test
    public void testHWRuleEx1() {
        // From
        // http://www.archives.gov/research_room/genealogy/census/soundex.html:
        // Ashcraft is coded A-261 (A, 2 for the S, C ignored, 6 for the R, 1
        // for the F). It is not coded A-226.
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcraft"));
        Assert.assertEquals("A261", this.getStringEncoder().encode("Ashcroft"));
        Assert.assertEquals("Y330", this.getStringEncoder().encode("yehudit"));
        Assert.assertEquals("Y330", this.getStringEncoder().encode("yhwdyt"));
    }
}
