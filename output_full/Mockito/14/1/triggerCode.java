/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.mockito.Mock;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
public class VerifyingWithAnExtraCallToADifferentMockTest extends TestBase {
    @Test 
    public void shouldAllowVerifyingWhenOtherMockCallIsInTheSameLine() {
        //given
        when(mock.otherMethod()).thenReturn("foo");
        
        //when
        mockTwo.simpleMethod("foo");
        
        //then
        verify(mockTwo).simpleMethod(mock.otherMethod());
    }
}
