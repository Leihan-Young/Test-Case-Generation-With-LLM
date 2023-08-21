/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.MockUtil;
import org.mockitoutil.TestBase;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
public class MockInjectionUsingSetterOrPropertyTest extends TestBase {
    private SuperUnderTesting superUnderTestWithoutInjection = new SuperUnderTesting();
    @InjectMocks private SuperUnderTesting superUnderTest = new SuperUnderTesting();
    @InjectMocks private BaseUnderTesting baseUnderTest = new BaseUnderTesting();
    @InjectMocks private SubUnderTesting subUnderTest = new SubUnderTesting();
    @InjectMocks private OtherBaseUnderTesting otherBaseUnderTest = new OtherBaseUnderTesting();
    @InjectMocks private OtherSuperUnderTesting otherSuperUnderTesting = new OtherSuperUnderTesting();
    private BaseUnderTesting baseUnderTestingInstance = new BaseUnderTesting();
    @InjectMocks private BaseUnderTesting initializedBase = baseUnderTestingInstance;
    @InjectMocks private BaseUnderTesting notInitializedBase;
    @Spy @InjectMocks private SuperUnderTesting initializedSpy = new SuperUnderTesting();
    @Spy @InjectMocks private SuperUnderTesting notInitializedSpy;
    @Mock private Map map;
    @Mock private List list;
    @Mock private Set histogram1;
    @Mock private Set histogram2;
    @Mock private A candidate2;
    @Spy private TreeSet searchTree = new TreeSet();
    private MockUtil mockUtil = new MockUtil();
    @Test
	public void shouldInsertFieldWithCorrectNameWhenMultipleTypesAvailable() {
		MockitoAnnotations.initMocks(this);
		assertNull(otherSuperUnderTesting.candidate1);
		assertNotNull(otherSuperUnderTesting.candidate2);
	}
}
