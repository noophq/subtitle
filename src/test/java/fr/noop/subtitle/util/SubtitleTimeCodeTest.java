/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.util;

import static org.junit.Assert.*;

import org.junit.*;

import java.security.InvalidParameterException;

/**
 * Created by clebeaupin on 07/10/15.
 */
public class SubtitleTimeCodeTest  {
    private SubtitleTimeCode tested = new SubtitleTimeCode(1, 23, 12, 10);

    @Test
    public void testToString() throws Exception {
        assertEquals("01:23:12.010", tested.toString());
    }

    @Test
    public void testGetHour() throws Exception {
        assertEquals(1, tested.getHour());
    }

    @Test
    public void testSetHour() throws Exception {
        tested.setHour(2);
        assertEquals(2, tested.getHour());
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetHourException() throws Exception {
        tested.setHour(-1);
    }

    @Test
    public void testGetMinute() throws Exception {
        assertEquals(23, tested.getMinute());
    }

    @Test
    public void testSetMinute() throws Exception {
        tested.setMinute(50);
        assertEquals(50, tested.getMinute());
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetMinuteException1() throws Exception {
        tested.setMinute(-1);
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetMinuteException2() throws Exception {
        tested.setMinute(60);
    }

    @Test
    public void testGetSecond() throws Exception {
        assertEquals(12, tested.getSecond());
    }

    @Test
    public void testSetSecond() throws Exception {
        tested.setSecond(50);
        assertEquals(50, tested.getSecond());
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetSecondException1() throws Exception {
        tested.setSecond(-1);
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetSecondException2() throws Exception {
        tested.setSecond(60);
    }

    @Test
    public void testGetMillisecond() throws Exception {
        assertEquals(10, tested.getMillisecond());
    }

    @Test
    public void testSetMillisecond() throws Exception {
        tested.setMillisecond(50);
        assertEquals(50, tested.getMillisecond());
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetMillisecondException1() throws Exception {
        tested.setMillisecond(-1);
    }

    @Test (expected = InvalidParameterException.class)
    public void testSetMillisecondException2() throws Exception {
        tested.setMillisecond(1000);
    }

    @Test
    public void testGetTime() throws Exception {
        assertEquals(4992010, tested.getTime());
    }

    @Test
    public void testCompareTo() throws Exception {
        assertEquals(0, tested.compareTo(new SubtitleTimeCode(1, 23, 12, 10)));
        assertEquals(1, tested.compareTo(new SubtitleTimeCode(1, 22, 12, 10)));
        assertEquals(-1, tested.compareTo(new SubtitleTimeCode(1, 24, 12, 10)));
    }

    @Test
    public void testSubtract() throws Exception {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(0, 13, 9, 7);
        assertEquals(expected.getTime(), tested.subtract(toSubtract).getTime());
    }
}