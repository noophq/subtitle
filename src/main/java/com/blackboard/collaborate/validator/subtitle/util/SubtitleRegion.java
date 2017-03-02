/*
 * Title: SubtitleRegion
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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by clebeaupin on 22/09/15.
 *
 * Implementation of a region
 * All units (x, y, width, height) are defined in percentage
 */
@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class SubtitleRegion {
    // Vertical align
    public enum VerticalAlign {
        TOP,
        BOTTOM
    }

    private float x; // x coordinate of the region
    private float y; // y coordinate of the region
    private float width; // Width of the region
    private float height; // Height of the region
    private VerticalAlign verticalAlign; // Vertical align

    public SubtitleRegion(SubtitleRegion subtitleRegion) {
        this.x = subtitleRegion.getX();
        this.y = subtitleRegion.getY();
        this.width = subtitleRegion.getWidth();
        this.height = subtitleRegion.getHeight();
        this.verticalAlign = subtitleRegion.getVerticalAlign();
    }

    public SubtitleRegion(float y, float height) {
        this(0, y, 100, height, VerticalAlign.BOTTOM);
    }

    public SubtitleRegion(float y, float height, VerticalAlign verticalAlign) {
        this(0, y, 100, height, verticalAlign);
    }

    public SubtitleRegion(float x, float y, float width, float height) {
        this(x, y, width, height, VerticalAlign.BOTTOM);
    }

    public void setX(float x) {
        if (x < 0.0f || x > 100.0f) {
            throw new IllegalArgumentException("X value must be defined in percentage between 0 and 100");
        }

        this.x = x;
    }

    public void setY(float y) {
        if (y < 0.0f || y > 100.0f) {
            throw new IllegalArgumentException("Y value must be defined in percentage between 0 and 100");
        }

        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

}
