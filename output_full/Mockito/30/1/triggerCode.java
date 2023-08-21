/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.defaultanswers;
import org.junit.Test;
import org.mockito.exceptions.verification.SmartNullPointerException;
import org.mockito.stubbing.Answer;
import org.mockitoutil.TestBase;
public class ReturnsSmartNullsTest extends TestBase {
    @Test
	public void shouldPrintTheParametersOnSmartNullPointerExceptionMessage() throws Throwable {
    	Answer<Object> answer = new ReturnsSmartNulls();

        Foo smartNull = (Foo) answer.answer(invocationOf(Foo.class, "withArgs", "oompa", "lumpa"));

        try {
            smartNull.get();
            fail();
        } catch (SmartNullPointerException ex) {
        	String message = ex.getMessage();
        	assertTrue("Exception message should include oompa and lumpa, but was: " + message, message.contains("oompa, lumpa"));
        }
	}
}
