/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;
import org.fest.assertions.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockitoutil.TestBase;
import java.util.*;
import static org.mockito.Mockito.*;
public class SpyAnnotationTest extends TestBase {
    @Rule public final ExpectedException shouldThrow = ExpectedException.none();
    @Test
    public void should_spy_inner_class() throws Exception {
    	 
     class WithMockAndSpy {
    		@Spy private InnerStrength strength;
    		@Mock private List<String> list;

            abstract class InnerStrength {
            	private final String name;

            	InnerStrength() {
            		// Make sure that @Mock fields are always injected before @Spy fields.
            		assertNotNull(list);
            		// Make sure constructor is indeed called.
            		this.name = "inner";
            	}
            	
            	abstract String strength();
            	
            	String fullStrength() {
            		return name + " " + strength();
            	}
            }
    	}
		WithMockAndSpy outer = new WithMockAndSpy();
        MockitoAnnotations.initMocks(outer);
        when(outer.strength.strength()).thenReturn("strength");
        assertEquals("inner strength", outer.strength.fullStrength());
    }
}
