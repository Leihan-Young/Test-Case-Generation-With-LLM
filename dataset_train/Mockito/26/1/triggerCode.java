/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.progress;
import org.junit.Test;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
public class HandyReturnValuesTest {
    private HandyReturnValues h = new HandyReturnValues();
    @Test public void should_default_values_for_primitive() {
        assertThat(h.returnFor(boolean.class)).isFalse();
        assertThat(h.returnFor(char.class)).isEqualTo('\u0000');
        assertThat(h.returnFor(byte.class)).isEqualTo((byte) 0);
        assertThat(h.returnFor(short.class)).isEqualTo((short) 0);
        assertThat(h.returnFor(int.class)).isEqualTo(0);
        assertThat(h.returnFor(long.class)).isEqualTo(0L);
        assertThat(h.returnFor(float.class)).isEqualTo(0.0F);
        assertThat(h.returnFor(double.class)).isEqualTo(0.0D);
    }
}
