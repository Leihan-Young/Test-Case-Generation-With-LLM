package org.mockitousage.bugs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
/**
 * Issue 211 : @InjectMocks should carry out their work by the method (and not by field) if available 
 */
public class InjectMocksShouldTryPropertySettersFirstBeforeFieldAccessTest {
    @Test
    public void shouldInjectUsingPropertySetterIfAvailable() {
        assertTrue(awaitingInjection.propertySetterUsed);
    }
}
