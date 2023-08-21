/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;
import org.fest.assertions.Assertions;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.*;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Arrays.asList;
public class InvocationMatcherTest extends TestBase {
    private InvocationMatcher simpleMethod;
    @Mock private IMethods mock;
    @Test  // like using several time the captor in the vararg
    public void should_capture_arguments_when_args_count_does_NOT_match() throws Exception {
        //given
        mock.varargs();
        Invocation invocation = getLastInvocation();

        //when
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, (List) asList(new LocalizedMatcher(AnyVararg.ANY_VARARG)));

        //then
        invocationMatcher.captureArgumentsFrom(invocation);
    }
}
