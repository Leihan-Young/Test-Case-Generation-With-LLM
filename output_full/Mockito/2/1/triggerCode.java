package org.mockito.internal.util;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.exceptions.misusing.FriendlyReminderException;
import org.mockitoutil.TestBase;
import static org.hamcrest.CoreMatchers.is;
public class TimerTest extends TestBase {
    private void oneMillisecondPasses() throws InterruptedException {
        Thread.sleep(1);
    }
    @Test
    public void should_throw_friendly_reminder_exception_when_duration_is_negative() {
        try {
            new Timer(-1);
            Assert.fail("It is forbidden to create timer with negative value of timer's duration.");
        } catch (FriendlyReminderException e) {
            Assert.assertTrue(true);
        }
    }
}
