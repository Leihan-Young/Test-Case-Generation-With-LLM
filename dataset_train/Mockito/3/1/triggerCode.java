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
    @Test
    public void should_capture_varargs_as_vararg() throws Exception {
        //given
        mock.mixedVarargs(1, "a", "b");
        Invocation invocation = getLastInvocation();
        CapturingMatcher m = new CapturingMatcher();
        InvocationMatcher invocationMatcher = new InvocationMatcher(invocation, (List) asList(new Equals(1), new LocalizedMatcher(m)));

        //when
        invocationMatcher.captureArgumentsFrom(invocation);

        //then
        Assertions.assertThat(m.getAllValues()).containsExactly("a", "b");
    }
}
