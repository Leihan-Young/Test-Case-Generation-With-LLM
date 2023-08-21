/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.invocation.Invocation;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockitoutil.TestBase;
public class AnswersValidatorTest extends TestBase {
    private AnswersValidator validator = new AnswersValidator();
    private Invocation invocation = new InvocationBuilder().method("canThrowException").toInvocation();
    @Test
    public void shouldFailWhenCallingRealMethodOnIterface() throws Throwable {
        //given
        Invocation inovcationOnIterface = new InvocationBuilder().method("simpleMethod").toInvocation();
        try {
            //when
            validator.validate(new CallsRealMethods(), inovcationOnIterface);
            //then
            fail();
        } catch (MockitoException e) {}
    }
}
