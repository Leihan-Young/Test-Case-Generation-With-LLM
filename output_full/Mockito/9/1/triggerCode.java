package org.mockitousage.constructor;
import java.util.List;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.mock.SerializableMode;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;
import static org.mockito.Mockito.*;
public class CreatingMocksWithConstructorTest extends TestBase {
    @Test
    public void abstractMethodStubbed() {
    	AbstractThing thing = spy(AbstractThing.class);
    	when(thing.name()).thenReturn("me");
    	assertEquals("abstract me", thing.fullName());
    }
}
