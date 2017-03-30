package org.arquillian.cube.q.api;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QRunConditionTest {

    @Test
    public void shouldIterate() {
        final Q.IterationRunCondition iterate = Q.IterationRunCondition.times(2);

        assertThat(iterate.isExecutable(), is(true));
        assertThat(iterate.isExecutable(), is(true));
        assertThat(iterate.isExecutable(), is(false));
    }

    @Test
    public void shouldApplyDuration() throws InterruptedException {
        final Q.DurationRunCondition during = Q.DurationRunCondition.during(5, TimeUnit.SECONDS);

        assertThat(during.isExecutable(), is(true));
        TimeUnit.SECONDS.sleep(6);
        assertThat(during.isExecutable(), is(false));
    }
}
