package pro.taskana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of TimeInterval.
 */
public class TimeIntervalTest {

    Instant time1 = Instant.ofEpochSecond(1000L);
    Instant time2 = Instant.ofEpochSecond(2000L);
    Instant time3 = Instant.ofEpochSecond(3000L);
    Instant time4 = Instant.ofEpochSecond(4000L);
    Instant time5 = Instant.ofEpochSecond(5000L);
    TimeInterval timeInterval;

    @Before
    public void setUp() {
        timeInterval = new TimeInterval(time2, time4);
    }

    @Test
    public void contains() {
        assertTrue(timeInterval.contains(time3));
        assertFalse(timeInterval.contains(time5));
        assertFalse(timeInterval.contains(time1));
        assertFalse(timeInterval.contains(null));
    }

    @Test
    public void isValid() {

        assertTrue(new TimeInterval(time1, time2).isValid());
        assertTrue(new TimeInterval(time1, null).isValid());
        assertTrue(new TimeInterval(null, time1).isValid());
    }

    @Test
    public void isNotValid() {

        assertFalse(new TimeInterval(null, null).isValid());
        assertFalse(new TimeInterval(time2, time1).isValid());
    }

    @Test
    public void getBegin() {
        assertEquals(time2, timeInterval.getBegin());
    }

    @Test
    public void setBegin() {
        timeInterval.setBegin(time4);
        assertEquals(time4, timeInterval.getBegin());
    }

    @Test
    public void getEnd() {
        assertEquals(time4, timeInterval.getEnd());
    }

    @Test
    public void setEnd() {
        timeInterval.setEnd(time5);
        assertEquals(time5, timeInterval.getEnd());
    }

    @Test
    public void toString1() {
        assertNotNull(timeInterval.toString());
        assertNotNull(new TimeInterval(null, null).toString());
    }

    @Test
    public void hashCode1() {

        TimeInterval timeInterval = new TimeInterval(time2, time4);
        TimeInterval timeInterval2 = new TimeInterval(time2, time4);
        assertEquals(timeInterval.hashCode(), timeInterval2.hashCode());
    }

    @Test
    public void equals1() {
        TimeInterval timeInterval = new TimeInterval(time2, time4);
        TimeInterval timeInterval2 = new TimeInterval(time2, time4);

        assertEquals(timeInterval, timeInterval2);
        assertEquals(timeInterval, timeInterval);
        assertNotEquals(timeInterval, null);
        assertNotEquals(timeInterval, new Object());
    }
}
