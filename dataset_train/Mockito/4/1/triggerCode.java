/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.exceptions;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.TooLittleActualInvocations;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.internal.exceptions.VerificationAwareInvocation;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
import java.lang.reflect.Field;
import java.util.Collections;
import static org.mockito.Mockito.mock;
public class ReporterTest extends TestBase {
    private Field someField() {
        return Mockito.class.getDeclaredFields()[0];
    }
    @Test(expected = VerificationInOrderFailure.class)
    public void can_use_print_mock_name_even_when_mock_bogus_default_answer_and_when_reporting_no_more_interaction_wanted_in_order() throws Exception {
        Invocation invocation_with_bogus_default_answer = new InvocationBuilder().mock(mock(IMethods.class, new Returns(false))).toInvocation();
        new Reporter().noMoreInteractionsWantedInOrder(invocation_with_bogus_default_answer);
    }
}
