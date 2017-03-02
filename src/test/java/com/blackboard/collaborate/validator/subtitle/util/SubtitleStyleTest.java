/*
 * Title: SubtitleStyleTest
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by clebeaupin on 08/10/15.
 */
public class SubtitleStyleTest {
    private SubtitleStyle tested;

    @Before
    public void setUp() {
        tested = new SubtitleStyle();
        tested.setTextAlign(SubtitleStyle.TextAlign.CENTER);
        tested.setDirection(SubtitleStyle.Direction.LTR);
        tested.setFontStyle(SubtitleStyle.FontStyle.ITALIC);
        tested.setTextDecoration(SubtitleStyle.TextDecoration.UNDERLINE);
        tested.setColor("white");
    }

    @Test
    public void testGetDirection() {
        assertEquals(SubtitleStyle.Direction.LTR, tested.getDirection());
    }

    @Test
    public void testSetDirection() {
        tested.setDirection(SubtitleStyle.Direction.RTL);
        assertEquals(SubtitleStyle.Direction.RTL, tested.getDirection());
    }

    @Test
    public void testGetTextAlign() {
        assertEquals(SubtitleStyle.TextAlign.CENTER, tested.getTextAlign());
    }

    @Test
    public void testSetTextAlign() {
        tested.setTextAlign(SubtitleStyle.TextAlign.RIGHT);
        assertEquals(SubtitleStyle.TextAlign.RIGHT, tested.getTextAlign());
    }

    @Test
    public void testGetFontStyle() {
        assertEquals(SubtitleStyle.FontStyle.ITALIC, tested.getFontStyle());
    }

    @Test
    public void testSetFontStyle() {
        tested.setFontStyle(SubtitleStyle.FontStyle.OBLIQUE);
        assertEquals(SubtitleStyle.FontStyle.OBLIQUE, tested.getFontStyle());
    }

    @Test
    public void testGetTextDecoration() {
        assertEquals(SubtitleStyle.TextDecoration.UNDERLINE, tested.getTextDecoration());
    }

    @Test
    public void testSetTextDecoration() {
        tested.setTextDecoration(SubtitleStyle.TextDecoration.LINE_THROUGH);
        assertEquals(SubtitleStyle.TextDecoration.LINE_THROUGH, tested.getTextDecoration());
    }

    @Test
    public void testGetColor() {
        assertEquals("white", tested.getColor());
    }

    @Test
    public void testSetColor() {
        tested.setColor("black");
        assertEquals("black", tested.getColor());
    }

    @Test
    public void testGetProperty() {
        assertEquals(SubtitleStyle.Direction.LTR, tested.getProperty(SubtitleStyle.Property.DIRECTION));
        assertEquals(SubtitleStyle.TextAlign.CENTER, tested.getProperty(SubtitleStyle.Property.TEXT_ALIGN));
        assertEquals(SubtitleStyle.FontStyle.ITALIC, tested.getProperty(SubtitleStyle.Property.FONT_STYLE));
        assertEquals(SubtitleStyle.TextDecoration.UNDERLINE, tested.getProperty(SubtitleStyle.Property.TEXT_DECORATION));
        assertEquals("white", tested.getProperty(SubtitleStyle.Property.COLOR));
    }
}