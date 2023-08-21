package org.mockito.internal.creation.instance;
import org.junit.Test;
import org.mockitoutil.TestBase;
public class ConstructorInstantiatorTest extends TestBase {
    @Test public void creates_instances_of_inner_classes() {
        assertEquals(new ConstructorInstantiator(this).newInstance(SomeInnerClass.class).getClass(), SomeInnerClass.class);
        assertEquals(new ConstructorInstantiator(new ChildOfThis()).newInstance(SomeInnerClass.class).getClass(), SomeInnerClass.class);
    }
}
