package org.mockito.internal.creation;
import org.junit.Before;
import org.junit.Test;
import org.mockitoutil.TestBase;
import java.lang.reflect.Method;
public class DelegatingMethodTest extends TestBase {
    private Method someMethod, otherMethod;
    private DelegatingMethod delegatingMethod;
    @Test
    public void equals_should_return_true_when_equal() throws Exception {
        DelegatingMethod equal = new DelegatingMethod(someMethod);
        assertTrue(delegatingMethod.equals(equal));
    }
}
