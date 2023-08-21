/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification.argumentmatching;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.internal.matchers.Equals;
import org.mockitoutil.TestBase;
public class ArgumentMatchingToolTest extends TestBase {
    private ArgumentMatchingTool tool = new ArgumentMatchingTool();
    @Test
    public void shouldWorkFineWhenGivenArgIsNull() {
        //when
        Integer[] suspicious = tool.getSuspiciouslyNotMatchingArgsIndexes((List) Arrays.asList(new Equals(20)), new Object[] {null});
        
        //then
        assertEquals(0, suspicious.length);
    }
}
