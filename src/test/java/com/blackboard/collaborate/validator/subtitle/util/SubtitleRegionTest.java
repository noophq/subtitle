/*
 * Title: SubtitleRegionTest
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

/**
 * Created by clebeaupin on 08/10/15.
 */
public class SubtitleRegionTest {
    private final SubtitleRegion tested = new SubtitleRegion(10, 20, 80, 10);

    @Test
    public void testGetX() {
        assertEquals(1000, (int) (tested.getX() * 100));
    }

    @Test
    public void testSetX() {
        tested.setX(12.5f);
        assertEquals(1250, (int) (tested.getX() * 100));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetXException1() throws Exception {
        tested.setX(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetXException2() throws Exception {
        tested.setX(101);
    }

    @Test
    public void testGetY() {
        assertEquals(2000, (int) tested.getY()*100);
    }

    @Test
    public void testSetY() {
        tested.setY(22.5f);
        assertEquals(2250, (int) (tested.getY() * 100));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetYException1() throws Exception {
        tested.setX(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSetYException2() throws Exception {
        tested.setX(101);
    }
}