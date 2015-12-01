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

/**
 * Created by clebeaupin on 08/10/15.
 */
public class SubtitleStyleTest {
    private SubtitleStyle tested;

    @Before
    public void setUp() throws Exception {
        tested = new SubtitleStyle();
        tested.setTextAlign(SubtitleStyle.TextAlign.CENTER);
        tested.setDirection(SubtitleStyle.Direction.LTR);
        tested.setFontStyle(SubtitleStyle.FontStyle.ITALIC);
        tested.setTextDecoration(SubtitleStyle.TextDecoration.UNDERLINE);
        tested.setColor("white");
    }

    @Test
    public void testGetDirection() throws Exception {
        assertEquals(SubtitleStyle.Direction.LTR, tested.getDirection());
    }

    @Test
    public void testSetDirection() throws Exception {
        tested.setDirection(SubtitleStyle.Direction.RTL);
        assertEquals(SubtitleStyle.Direction.RTL, tested.getDirection());
    }

    @Test
    public void testGetTextAlign() throws Exception {
        assertEquals(SubtitleStyle.TextAlign.CENTER, tested.getTextAlign());
    }

    @Test
    public void testSetTextAlign() throws Exception {
        tested.setTextAlign(SubtitleStyle.TextAlign.RIGHT);
        assertEquals(SubtitleStyle.TextAlign.RIGHT, tested.getTextAlign());
    }

    @Test
    public void testGetFontStyle() throws Exception {
        assertEquals(SubtitleStyle.FontStyle.ITALIC, tested.getFontStyle());
    }

    @Test
    public void testSetFontStyle() throws Exception {
        tested.setFontStyle(SubtitleStyle.FontStyle.OBLIQUE);
        assertEquals(SubtitleStyle.FontStyle.OBLIQUE, tested.getFontStyle());
    }

    @Test
    public void testGetTextDecoration() throws Exception {
        assertEquals(SubtitleStyle.TextDecoration.UNDERLINE, tested.getTextDecoration());
    }

    @Test
    public void testSetTextDecoration() throws Exception {
        tested.setTextDecoration(SubtitleStyle.TextDecoration.LINE_THROUGH);
        assertEquals(SubtitleStyle.TextDecoration.LINE_THROUGH, tested.getTextDecoration());
    }

    @Test
    public void testGetColor() throws Exception {
        assertEquals("white", tested.getColor());
    }

    @Test
    public void testSetColor() throws Exception {
        tested.setColor("black");
        assertEquals("black", tested.getColor());
    }

    @Test
    public void testGetProperty() throws Exception {
        assertEquals(SubtitleStyle.Direction.LTR, tested.getProperty(SubtitleStyle.Property.DIRECTION));
        assertEquals(SubtitleStyle.TextAlign.CENTER, tested.getProperty(SubtitleStyle.Property.TEXT_ALIGN));
        assertEquals(SubtitleStyle.FontStyle.ITALIC, tested.getProperty(SubtitleStyle.Property.FONT_STYLE));
        assertEquals(SubtitleStyle.TextDecoration.UNDERLINE, tested.getProperty(SubtitleStyle.Property.TEXT_DECORATION));
        assertEquals("white", tested.getProperty(SubtitleStyle.Property.COLOR));
    }
}