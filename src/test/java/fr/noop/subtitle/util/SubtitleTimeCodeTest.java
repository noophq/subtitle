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
        assertTrue(tested.compareTo(new SubtitleTimeCode(1, 22, 12, 10)) > 0);
        assertTrue(tested.compareTo(new SubtitleTimeCode(1, 24, 12, 10)) < 0);
    }

    @Test
    public void testOffsetPositiveCompareTo() throws Exception {
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43, 0)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 33, 10)));
        assertTrue(testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 20, 7, 33, 10)) > 0);
        assertTrue(testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 30, 7, 33, 10)) < 0);
    }

    @Test
    public void testOffsetNegativeCompareTo() throws Exception {
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10, 0)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, -420000)));
        assertTrue(testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 20, 12, 10, 0)) > 0);
        assertTrue(testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, 0)) < 0);
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