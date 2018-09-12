/*
 * Title: StlTti
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.stl;

import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by clebeaupin on 21/09/15.
 */
@Getter
@Setter
public class StlTti {
    private short sgn; // Subtitle Group Number
    private int sn; // Subtitle Number
    private short ebn; // Extension Block Number
    private short cs; // Cumulative Status
    private SubtitleTimeCode tci; // Time Code In
    private SubtitleTimeCode tco; // Time Code Out
    private short vp; // Vertical Position
    private Jc jc; // Justification Code
    private short cf; // Comment Flag
    private String tf; // Text Field

    // List of colors defined in STL EBU
    public enum TextColor {
        ALPHA_BLACK(0x00, "black"),
        ALPHA_READ(0x01, "red"),
        ALPHA_GREEN(0x02, "green"),
        ALPHA_YELLOW(0x03, "yellow"),
        ALPHA_BLUE(0x04, "blue"),
        ALPHA_MAGENTA(0x05, "magenta"),
        ALPHA_CYAN(0x06, "cyan"),
        ALPHA_WHITE(0x07, "white"),
        MOSAIC_BLACK(0x10, "black"),
        MOSAIC_READ(0x11, "red"),
        MOSAIC_GREEN(0x12, "green"),
        MOSAIC_YELLOW(0x13, "yellow"),
        MOSAIC_BLUE(0x14, "blue"),
        MOSAIC_MAGENTA(0x15, "magenta"),
        MOSAIC_CYAN(0x16, "cyan"),
        MOSAIC_WHITE(0x17, "white");

        private int value;
        private String color;

        TextColor(int value, String color) {
            this.value = value;
            this.color = color;
        }

        public int getValue() {
            return this.value;
        }

        public String getColor() {
            return this.color;
        }

        public static boolean hasEnum(int value) {
            for (TextColor v : values()) {
                if (v.getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        public static TextColor getEnum(int value) {
            for (TextColor v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    // List of text styles
    public enum TextStyle {
        ITALIC_ON(0x80),
        ITALIC_OFF(0x81),
        UNDERLINE_ON(0x82),
        UNDERLINE_OFF(0x83),
        BOXING_ON(0x84),
        BOXING_OFF(0x85);

        private int value;

        TextStyle(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static boolean hasEnum(int value) {
            for (TextStyle v : values()) {
                if (v.getValue() == value) {
                    return true;
                }
            }
            return false;
        }

        public static TextStyle getEnum(int value) {
            for (TextStyle v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    // Justification Code (JC)
    // Offset: 14
    // Length: 1 bytes
    public enum Jc {
        NONE(0x00),
        LEFT(0x01),
        CENTER(0x02),
        RIGHT(0x03);

        private int value;

        Jc(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Jc getEnum(int value) {
            for (Jc v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
