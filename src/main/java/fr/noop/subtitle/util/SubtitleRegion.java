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

import java.security.InvalidParameterException;

/**
 * Created by clebeaupin on 22/09/15.
 *
 * Implementation of a region
 * All units (x, y, width, height) are defined in percentage
 */
public class SubtitleRegion {
    // Vertical align
    public enum VerticalAlign {
        TOP,
        MIDDLE,
        BOTTOM;
    }

    private float x; // x coordinate of the region
    private float y; // y coordinate of the region
    private float width; // Width of the region
    private float height; // Height of the region
    private VerticalAlign verticalAlign; // Vertical align
    private int verticalPosition; // Vertical position

    public SubtitleRegion(SubtitleRegion subtitleRegion) {
        this.x = subtitleRegion.getX();
        this.y = subtitleRegion.getY();
        this.width = subtitleRegion.getWidth();
        this.height = subtitleRegion.getHeight();
        this.verticalAlign = subtitleRegion.getVerticalAlign();
        this.verticalPosition = subtitleRegion.getVerticalPosition();
    }

    public SubtitleRegion(float y, float height) {
        this(0, y, 100, height, VerticalAlign.BOTTOM, 0);
    }

    public SubtitleRegion(float y, float height, VerticalAlign verticalAlign) {
        this(0, y, 100, height, verticalAlign, 0);
    }

    public SubtitleRegion(float x, float y, float width, float height) {
        this(x, y, width, height, VerticalAlign.BOTTOM, 0);
    }

    public SubtitleRegion(float x, float y, float width, float height, VerticalAlign verticalAlign, int verticalPosition) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.verticalAlign = verticalAlign;
        this.verticalPosition = verticalPosition;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        if (x < 0.0f || x > 100.0f) {
            throw new InvalidParameterException("X value must be defined in percentage between 0 and 100");
        }

        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        if (y < 0.0f || y > 100.0f) {
            throw new InvalidParameterException("Y value must be defined in percentage between 0 and 100");
        }

        this.y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public VerticalAlign getVerticalAlign() {
        return this.verticalAlign;
    }

    public void setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public int getVerticalPosition() {
        return this.verticalPosition;
    }

    public void setVerticalPosition(int verticalPosition) {
        this.verticalPosition = verticalPosition;
    }

    @Override
    public boolean equals(Object object) {
        //check for self-comparison
        if (this == object) {
            return true;
        }

        // Check that object is SubtitleRegion
        if (!(object instanceof SubtitleRegion)) {
            return false;
        }

        SubtitleRegion region = (SubtitleRegion) object;

        if (this.getX() == region.getX() &&
                this.getY() == region.getY() &&
                this.getWidth() == region.getWidth() &&
                this.getHeight() == region.getHeight() &&
                this.getVerticalAlign() == region.getVerticalAlign()) {
            return true;
        }

        return false;
    }
}
