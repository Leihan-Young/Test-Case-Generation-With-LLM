package org.mockito.internal.util.reflection;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
public class GenericMasterTest {
    private Field field(String fieldName) throws SecurityException, NoSuchFieldException {
        return this.getClass().getDeclaredField(fieldName);
    }
    @Test
    public void shouldDealWithNestedGenerics() throws Exception {
        assertEquals(Set.class, m.getGenericType(field("nested")));
        assertEquals(Set.class, m.getGenericType(field("multiNested")));
    }
}
