/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;
import org.junit.Test;
import org.mockito.invocation.Invocation;
import org.mockitoutil.TestBase;
import java.util.*;
import static org.mockito.Mockito.mock;
public class ReturnsEmptyValuesTest extends TestBase {
    @Test public void should_return_zero_if_mock_is_compared_to_itself() {
        //given
        Date d = mock(Date.class);
        d.compareTo(d);
        Invocation compareTo = this.getLastInvocation();

        //when
        Object result = values.answer(compareTo);

        //then
        assertEquals(0, result);
    }
}
