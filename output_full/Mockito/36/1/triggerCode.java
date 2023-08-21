/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;
import java.lang.reflect.Method;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.invocation.realmethod.RealMethod;
import org.mockito.internal.matchers.ArrayEquals;
import org.mockito.internal.matchers.Equals;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
public class InvocationTest extends TestBase {
    private Invocation invocation;
    @Test
    public void shouldScreamWhenCallingRealMethodOnInterface() throws Throwable {
        //given
        Invocation invocationOnInterface = new InvocationBuilder().toInvocation();

        try {
            //when
            invocationOnInterface.callRealMethod();
            //then
            fail();
        } catch(MockitoException e) {}
    }
}
