/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.*;
import org.mockito.internal.reporting.PrintingFriendlyInvocation;
import org.mockitousage.IMethods;
import static org.mockito.Matchers.anyVararg;
import static org.mockitoutil.ExtraMatchers.hasExactlyInOrder;
import org.mockitoutil.TestBase;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class InvocationMatcherTest extends TestBase {
    private InvocationMatcher simpleMethod;
    @Mock private IMethods mock; 
    @Test
    public void shouldMatchCaptureArgumentsWhenArgsCountDoesNOTMatch() throws Exception {
        //given
        mock.varargs();
        Invocation invocation = getLastInvocation();

        //when
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, (List) asList(new LocalizedMatcher(AnyVararg.ANY_VARARG)));

        //then
        invocationMatcher.captureArgumentsFrom(invocation);
    }
}
