/*
 * Title: SubtitleTimeCodeTest
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by clebeaupin on 07/10/15.
 */
public class SubtitleTimeCodeTest  {
    private final SubtitleTimeCode tested = new SubtitleTimeCode(1, 23, 12, 10);
    private final SubtitleTimeCode testedOffsetPositive = new SubtitleTimeCode(1, 23, 12, 10, 3775033);
    private final SubtitleTimeCode testedOffsetNegative = new SubtitleTimeCode(16, 23, 12, 10, -36000000);

    @Test
    public void testOver() {
        new SubtitleTimeCode(Integer.MAX_VALUE, 23, 12, 10);
    }
    
    @Test
    public void testToString() {
        assertEquals("01:23:12.010", tested.toString());
    }

    @Test
    public void testOffsetPositiveToString() {
        assertEquals("02:26:07.043", testedOffsetPositive.toString());
    }

    @Test
    public void testOffsetNegativeToString() {
        assertEquals("06:23:12.010", testedOffsetNegative.toString());
    }

    @Test
    public void testGetHour() {
        assertEquals(1, tested.getHour());
    }

    @Test
    public void testOffsetPositiveGetHour() {
        assertEquals(2, testedOffsetPositive.getHour());
    }

    @Test
    public void testOffsetNegativeGetHour() {
        assertEquals(6, testedOffsetNegative.getHour());
    }

    @Test
    public void testGetMinute() {
        assertEquals(23, tested.getMinute());
    }

    @Test
    public void testOffsetPositiveGetMinute() {
        assertEquals(26, testedOffsetPositive.getMinute());
    }

    @Test
    public void testOffsetNegativeGetMinute() {
        assertEquals(23, testedOffsetNegative.getMinute());
    }

    public void testGetSecond() {
        assertEquals(12, tested.getSecond());
    }

    public void testOffsetPositiveGetSecond() {
        assertEquals(7, testedOffsetPositive.getSecond());
    }

    public void testOffsetNegativeGetSecond() {
        assertEquals(12, testedOffsetNegative.getSecond());
    }

    @Test
    public void testGetMillisecond() {
        assertEquals(10, tested.getMillisecond());
    }

    @Test
    public void testOffsetPositiveGetMillisecond() {
        assertEquals(43, testedOffsetPositive.getMillisecond());
    }

    @Test
    public void testOffsetNegativeGetMillisecond() {
        assertEquals(10, testedOffsetNegative.getMillisecond());
    }

    @Test
    public void testGetTime() {
        assertEquals(4992010, tested.getTime());
    }

    @Test
    public void testOffsetPositiveGetTime() {
        assertEquals(8767043, testedOffsetPositive.getTime());
    }

    @Test
    public void testOffsetNegativeGetTime() {
        assertEquals(22992010, testedOffsetNegative.getTime());
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, tested.compareTo(new SubtitleTimeCode(1, 23, 12, 10)));
        assertTrue(tested.compareTo(new SubtitleTimeCode(1, 22, 12, 10)) > 0);
        assertTrue(tested.compareTo(new SubtitleTimeCode(1, 24, 12, 10)) < 0);
    }

    @Test
    public void testOffsetPositiveCompareTo() {
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 43, 0)));
        assertEquals(0, testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 26, 7, 33, 10)));
        assertTrue(testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 20, 7, 33, 10)) > 0);
        assertTrue(testedOffsetPositive.compareTo(new SubtitleTimeCode(2, 30, 7, 33, 10)) < 0);
    }

    @Test
    public void testOffsetNegativeCompareTo() {
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 23, 12, 10, 0)));
        assertEquals(0, testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, -420000)));
        assertTrue(testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 20, 12, 10, 0)) > 0);
        assertTrue(testedOffsetNegative.compareTo(new SubtitleTimeCode(6, 30, 12, 10, 0)) < 0);
    }

    @Test
    public void testSubtract() {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(0, 13, 9, 7);
        assertEquals(expected.getTime(), tested.subtract(toSubtract).getTime());
    }

    @Test
    public void testOffsetPositiveSubtract() {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 0, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(1, 16, 4, 40);
        assertEquals(expected.getTime(), testedOffsetPositive.subtract(toSubtract).getTime());
    }

    @Test
    public void testOffsetNegativeSubtract() {
        // Subtract 1 hour, 10 minutes, 3 seconds and 3 frames ::6, 23, 12, 10
        SubtitleTimeCode toSubtract = new SubtitleTimeCode(1, 10, 3, 0, 3);
        SubtitleTimeCode expected = new SubtitleTimeCode(5, 13, 9, 7, 0);
        assertEquals(expected.getTime(), testedOffsetNegative.subtract(toSubtract).getTime());
    }
}