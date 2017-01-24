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
    private SubtitleTimeCode testedOffsetPositive = new SubtitleTimeCode(1, 23, 12, 10, 3775033);
    private SubtitleTimeCode testedOffsetNegative = new SubtitleTimeCode(16, 23, 12, 10, -36000000);

    @Test
    public void testToString() throws Exception {
        assertEquals("01:23:12.010", tested.toString());
    }

    @Test
    public void testOffsetPositiveToString() throws Exception {
        assertEquals("02:26:07.043", testedOffsetPositive.toString());
    }

    @Test
    public void testOffsetNegativeToString() throws Exception {
        assertEquals("06:23:12.010", testedOffsetNegative.toString());
    }

    @Test
    public void testGetHour() throws Exception {
        assertEquals(1, tested.getHour());
    }

    @Test
    public void testOffsetPositiveGetHour() throws Exception {
        assertEquals(2, testedOffsetPositive.getHour());
    }

    @Test
    public void testOffsetNegativeGetHour() throws Exception {
        assertEquals(6, testedOffsetNegative.getHour());
    }

    @Test
    public void testSetHour() throws Exception {
        tested.setHour(2);
        assertEquals(2, tested.getHour());
    }

    @Test
    public void testOffsetPositiveSetHour() throws Exception {
        testedOffsetPositive.setHour(4);
        assertEquals(4, testedOffsetPositive.getHour());
    }

    @Test
    public void testOffsetNegativeSetHour() throws Exception {
        testedOffsetNegative.setHour(12);
        assertEquals(12, testedOffsetNegative.getHour());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetHourException() throws Exception {
        tested.setHour(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetHourException() throws Exception {
    	testedOffsetPositive.setHour(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetHourException() throws Exception {
    	testedOffsetNegative.setHour(-1);
    }

    @Test
    public void testGetMinute() throws Exception {
        assertEquals(23, tested.getMinute());
    }

    @Test
    public void testOffsetPositiveGetMinute() throws Exception {
        assertEquals(26, testedOffsetPositive.getMinute());
    }

    @Test
    public void testOffsetNegativeGetMinute() throws Exception {
        assertEquals(23, testedOffsetNegative.getMinute());
    }

    @Test
    public void testSetMinute() throws Exception {
        tested.setMinute(50);
        assertEquals(50, tested.getMinute());
    }

    @Test
    public void testOffsetPositiveSetMinute() throws Exception {
        testedOffsetPositive.setMinute(50);
        assertEquals(50, testedOffsetPositive.getMinute());
    }

    @Test
    public void testOffsetNegativeSetMinute() throws Exception {
        testedOffsetNegative.setMinute(50);
        assertEquals(50, testedOffsetNegative.getMinute());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetMinuteException1() throws Exception {
        tested.setMinute(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetMinuteException1() throws Exception {
    	testedOffsetPositive.setMinute(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetMinuteException1() throws Exception {
    	testedOffsetNegative.setMinute(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetMinuteException2() throws Exception {
        tested.setMinute(60);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetMinuteException2() throws Exception {
    	testedOffsetPositive.setMinute(60);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetMinuteException2() throws Exception {
    	testedOffsetNegative.setMinute(60);
    }

    public void testGetSecond() throws Exception {
        assertEquals(12, tested.getSecond());
    }

    public void testOffsetPositiveGetSecond() throws Exception {
        assertEquals(7, testedOffsetPositive.getSecond());
    }

    public void testOffsetNegativeGetSecond() throws Exception {
        assertEquals(12, testedOffsetNegative.getSecond());
    }

    @Test
    public void testSetSecond() throws Exception {
        tested.setSecond(50);
        assertEquals(50, tested.getSecond());
    }

    @Test
    public void testOffsetPositiveSetSecond() throws Exception {
        testedOffsetPositive.setSecond(50);
        assertEquals(50, testedOffsetPositive.getSecond());
    }

    @Test
    public void testOffsetNegativeSetSecond() throws Exception {
        testedOffsetNegative.setSecond(50);
        assertEquals(50, testedOffsetNegative.getSecond());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetSecondException1() throws Exception {
        tested.setSecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetSecondException1() throws Exception {
    	testedOffsetPositive.setSecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetSecondException1() throws Exception {
    	testedOffsetNegative.setSecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetSecondException2() throws Exception {
        tested.setSecond(60);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetSecondException2() throws Exception {
    	testedOffsetPositive.setSecond(60);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetSecondException2() throws Exception {
    	testedOffsetNegative.setSecond(60);
    }

    @Test
    public void testGetMillisecond() throws Exception {
        assertEquals(10, tested.getMillisecond());
    }

    @Test
    public void testOffsetPositiveGetMillisecond() throws Exception {
        assertEquals(43, testedOffsetPositive.getMillisecond());
    }

    @Test
    public void testOffsetNegativeGetMillisecond() throws Exception {
        assertEquals(10, testedOffsetNegative.getMillisecond());
    }

    @Test
    public void testSetMillisecond() throws Exception {
        tested.setMillisecond(50);
        assertEquals(50, tested.getMillisecond());
    }

    @Test
    public void testOffsetPositiveSetMillisecond() throws Exception {
        testedOffsetPositive.setMillisecond(50);
        assertEquals(50, testedOffsetPositive.getMillisecond());
    }

    @Test
    public void testOffsetNegativeSetMillisecond() throws Exception {
    	testedOffsetNegative.setMillisecond(50);
        assertEquals(50, testedOffsetNegative.getMillisecond());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetMillisecondException1() throws Exception {
        tested.setMillisecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetMillisecondException1() throws Exception {
    	testedOffsetPositive.setMillisecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetMillisecondException1() throws Exception {
    	testedOffsetNegative.setMillisecond(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetMillisecondException2() throws Exception {
        tested.setMillisecond(1000);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetPositiveSetMillisecondException2() throws Exception {
    	testedOffsetPositive.setMillisecond(1000);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOffsetNegativeSetMillisecondException2() throws Exception {
    	testedOffsetNegative.setMillisecond(1000);
    }

    @Test
    public void testGetTime() throws Exception {
        assertEquals(4992010, tested.getTime());
    }

    @Test
    public void testOffsetPositiveGetTime() throws Exception {
        assertEquals(8767043, testedOffsetPositive.getTime());
    }

    @Test
    public void testOffsetNegativeGetTime() throws Exception {
        assertEquals(22992010, testedOffsetNegative.getTime());
    }

    @Test
    public void testCompareTo() throws Exception {
        assertEquals(0, tested.compareTo(new SubtitleTimeCode(1, 23, 12, 10)));
        assertEquals(1, tested.compareTo(new SubtitleTimeCode(1, 22, 12, 10)));
        assertEquals(-1, tested.compareTo(new SubtitleTimeCode(1, 24, 12, 10)));
    }

    @Test
    public void testOffsetPositiveCompareTo() throws Exception {
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43, 0)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 33, 10)));
        assertEquals(1, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 20, 7, 33, 10)));
        assertEquals(-1, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 30, 7, 33, 10)));
    }

    @Test
    public void testOffsetNegativeCompareTo() throws Exception {
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10, 0)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, -420000)));
        assertEquals(1, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 20, 12, 10, 0)));
        assertEquals(-1, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, 0)));
    }

    @Test
    public void testSubtract() throws Exception {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(0, 13, 9, 7);
        assertEquals(expected.getTime(), tested.subtract(toSubtract).getTime());
    }

    @Test
    public void testOffsetPositiveSubtract() throws Exception {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 0, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(1, 16, 4, 40);
        assertEquals(expected.getTime(), testedOffsetPositive.subtract(toSubtract).getTime());
    }

    @Test
    public void testOffsetNegativeSubtract() throws Exception {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames ::6, 23, 12, 10
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 0, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(5, 13, 9, 7, 0);
        assertEquals(expected.getTime(), testedOffsetNegative.subtract(toSubtract).getTime());
    }
}