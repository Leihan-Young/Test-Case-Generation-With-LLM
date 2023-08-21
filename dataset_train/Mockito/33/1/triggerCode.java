/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;
import static org.mockito.Mockito.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;
public class InheritedGenericsPolimorphicCallTest extends TestBase {
    @Test
    public void shouldStubbingWork() {
        Mockito.when(iterable.iterator()).thenReturn(myIterator);
        Assert.assertNotNull(((Iterable) iterable).iterator());
        Assert.assertNotNull(iterable.iterator());
    }
}
