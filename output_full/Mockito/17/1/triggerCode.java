/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;
import static org.mockito.Mockito.*;
import java.io.*;
import java.util.*;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.internal.matchers.Any;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
public class MocksSerializationTest extends TestBase implements Serializable {
    private static final long serialVersionUID = 6160482220413048624L;
    @Test
    public void shouldBeSerializeAndHaveExtraInterfaces() throws Exception {
        //when
        IMethods mock = mock(IMethods.class, withSettings().serializable().extraInterfaces(List.class));
        IMethods mockTwo = mock(IMethods.class, withSettings().extraInterfaces(List.class).serializable());

        //then
        serializeAndBack((List) mock);
        serializeAndBack((List) mockTwo);
    }
}
