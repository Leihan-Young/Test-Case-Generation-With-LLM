package org.mockitousage.stubbing;
import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.Serializable;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.SimpleSerializationUtil;
public class DeepStubsSerializableTest {
    public static final boolean STUBBED_BOOLEAN_VALUE = true;
    public static final int STUBBED_INTEGER_VALUE = 999;
    @Test
    public void should_serialize_and_deserialize_mock_created_by_deep_stubs() throws Exception {
        // given
        SampleClass sampleClass = mock(SampleClass.class, withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS).serializable());
        when(sampleClass.getSample().isSth()).thenReturn(STUBBED_BOOLEAN_VALUE);
        when(sampleClass.getSample().getNumber()).thenReturn(STUBBED_INTEGER_VALUE);

        // when
        Object o = SimpleSerializationUtil.serializeAndBack(sampleClass);

        // then
        assertThat(o).isInstanceOf(SampleClass.class);
        SampleClass deserializedSample = (SampleClass) o;
        assertThat(deserializedSample.getSample().isSth()).isEqualTo(STUBBED_BOOLEAN_VALUE);
        assertThat(deserializedSample.getSample().getNumber()).isEqualTo(STUBBED_INTEGER_VALUE);
    }
}
