package org.mockitousage.bugs.deepstubs;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;
public class DeepStubFailingWhenGenricNestedAsRawTypeTest {
  @Test
  public void discoverDeepMockingOfGenerics() {
    MyClass1 myMock1 = mock(MyClass1.class, RETURNS_DEEP_STUBS);
    when(myMock1.getNested().getNested().returnSomething()).thenReturn("Hello World.");
  }
}
