package org.mockitousage.bugs;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
/**
 * In GH issue 99 : https://github.com/mockito/mockito/issues/99
 */
public class DeepStubsWronglyReportsSerializationProblemsTest {
    @Test
    public void should_not_raise_a_mockito_exception_about_serialization_when_accessing_deep_stub() {
        NotSerializableShouldBeMocked the_deep_stub = mock(ToBeDeepStubbed.class, RETURNS_DEEP_STUBS).getSomething();
        assertThat(the_deep_stub).isNotNull();
    }
}
