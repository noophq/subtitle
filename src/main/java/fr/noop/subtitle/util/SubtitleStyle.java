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

import fr.noop.subtitle.model.SubtitleObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clebeaupin on 05/10/15.
 */
public class SubtitleStyle {
    // Properties
    public enum Property {
        DIRECTION,
        TEXT_ALIGN,
        COLOR,
        FONT_STYLE,
        FONT_WEIGHT,
        TEXT_DECORATION;
    }

    // Direction
    public enum Direction {
        LTR,
        RTL;
    }

    // Text decoration
    public enum TextDecoration {
        NONE,
        UNDERLINE,
        OVERLINE,
        LINE_THROUGH;
    }

    // Text align
    public enum TextAlign {
        LEFT,
        RIGHT,
        CENTER;
    }

    // Text decoration
    public enum FontStyle {
        NORMAL,
        ITALIC,
        OBLIQUE;
    }

    // Text decoration
    public enum FontWeight {
        NORMAL,
        BOLD;
    }

    // Store all style properties
    private Map<Property, Object> properties = new HashMap<>();

    public SubtitleStyle() {}

    public SubtitleStyle(SubtitleStyle subtitleStyle) {
        for (Map.Entry<Property, Object> entry : subtitleStyle.getProperties().entrySet()) {
            this.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public Direction getDirection() {
        return (Direction) this.getProperty(Property.DIRECTION);
    }

    public void setDirection(Direction direction) {
        this.setProperty(Property.DIRECTION, direction);
    }

    public TextAlign getTextAlign() {

        return (TextAlign) this.getProperty(Property.TEXT_ALIGN);
    }

    public void setTextAlign(TextAlign textAlign) {
        this.setProperty(Property.TEXT_ALIGN, textAlign);
    }

    public String getColor() {
        return (String) this.getProperty(Property.COLOR);
    }

    public void setColor(String color) {
        this.setProperty(Property.COLOR, color);
    }

    public FontStyle getFontStyle() {
        return (FontStyle) this.getProperty(Property.FONT_STYLE);
    }

    public void setFontStyle(FontStyle fontStyle) {
        this.setProperty(Property.FONT_STYLE, fontStyle);
    }

    public TextDecoration getTextDecoration() {
        return (TextDecoration) this.getProperty(Property.TEXT_DECORATION);
    }

    public void setTextDecoration(TextDecoration textDecoration) {
        this.setProperty(Property.TEXT_DECORATION, textDecoration);
    }

    public Map<Property, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(HashMap<Property, Object> properties) {
        this.properties = properties;
    }

    public void setProperty(Property property, Object value) {
        this.properties.put(property, value);
    }

    public Object getProperty(Property property) {
        return this.properties.get(property);
    }

    /**
     * @return true if one of the property is initialized otherwise false
     */
    public boolean hasProperties() {
        return !this.properties.isEmpty();
    }

    public String buildSignature() {
        return "none";
    }
}
