/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;
import static org.mockito.Mockito.mock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Test;
import org.mockito.invocation.Invocation;
import org.mockitoutil.TestBase;
public class ReturnsEmptyValuesTest extends TestBase {
    @Test
    public void should_return_empty_iterable() throws Exception {
        assertFalse(((Iterable) values.returnValueFor(Iterable.class)).iterator().hasNext());
    }
}
