package org.mockitousage.bugs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.lang.reflect.Field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
public class InjectionByTypeShouldFirstLookForExactTypeThenAncestorTest {
    private static final Object REFERENCE = new Object();
    @Mock private Bean mockedBean;
    @InjectMocks private Service illegalInjectionExample = new Service();
    @Test
    public void mock_should_be_injected_once_and_in_the_best_matching_type() {
        assertSame(REFERENCE, illegalInjectionExample.mockShouldNotGoInHere);
        assertSame(mockedBean, illegalInjectionExample.mockShouldGoInHere);
    }
}
