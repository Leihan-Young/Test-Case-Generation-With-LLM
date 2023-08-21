/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;
import org.junit.Test;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
import static org.mockito.Mockito.*;
public class StubbingMocksThatAreConfiguredToReturnMocksTest extends TestBase {
    @Test
    public void shouldAllowStubbingMocksConfiguredWithRETURNS_MOCKS() {
        IMethods mock = mock(IMethods.class, RETURNS_MOCKS);
        when(mock.objectReturningMethodNoArgs()).thenReturn(null);
    }
}
