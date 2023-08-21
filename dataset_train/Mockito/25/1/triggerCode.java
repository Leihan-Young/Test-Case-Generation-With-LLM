/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;
import org.junit.Test;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
public class ReturnsGenericDeepStubsTest {
    @Test
    public void will_return_default_value_on_non_mockable_nested_generic() throws Exception {
        GenericsNest<?> genericsNest = mock(GenericsNest.class, RETURNS_DEEP_STUBS);
        ListOfInteger listOfInteger = mock(ListOfInteger.class, RETURNS_DEEP_STUBS);

        assertThat(genericsNest.returningNonMockableNestedGeneric().keySet().iterator().next()).isNull();
        assertThat(listOfInteger.get(25)).isEqualTo(0);
    }
}
