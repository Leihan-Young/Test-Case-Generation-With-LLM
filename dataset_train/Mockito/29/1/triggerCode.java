/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
public class NPEWithCertainMatchersTest extends TestBase {
    @Test(expected = AssertionError.class)
    public void shouldNotThrowNPEWhenNullPassedToSame() {
        mock.objectArgMethod("not null");

        verify(mock).objectArgMethod(same(null));
    }
}
