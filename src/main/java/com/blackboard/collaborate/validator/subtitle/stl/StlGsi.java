/*
 * Title: StlGsi
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

import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by clebeaupin on 21/09/15.
 */
@Getter
@Setter
public class StlGsi {
    private Cpn cpn; // Code Page Number
    private Dfc dfc; // Disk Format Code
    private Dsc dsc; // Display Standard Code
    private Cct cct; // Character Code Table number
    private int lc; // Language Code
    private String opt; // Original Programme Title
    private String oet; // Original Episode Title
    private String tpt; // Translated Programme Title
    private String tet; // Translated Episode Title
    private String tn; // Translator's Name
    private String tcd; // Translator's Contact Details
    private String slr; // Subtitle List Reference Code
    private Date cd; // Creation date (YYMMDD) (ISO 8601)
    private Date rd; // Revision Date (YYMMDD) (ISO 8601)
    private int rn; // Revision number
    private int tnb; // Total Number of Text and Timing Information (TTI) blocks
    private int tns; // Total Number of Subtitles
    private int tng; // Total Number of Subtitle Groups
    private int mnc; // Maximum Number of Displayable Characters in any text row
    private int mnr; // Maximum Number of Displayable Rows
    private short tcs; // Time Code Status
    private SubtitleTimeCode tcp; // Start-of-Programme
    private SubtitleTimeCode tcf; // Time Code: First In-Cue
    private short tnd; // Total Number of Disks
    private short dsn; // Disk Sequence Number
    private String co; // Country of Origin
    private String pub; // Publisher
    private String en; // Editor's Name
    private String ecd; // Editor's Contact Details
    private String uda; // User-Defined Area

    // Code Page Number (CPN)
    public enum Cpn {
        UNITED_STATES(0x343337),
        MULTILINGUAL(0x383530),
        PORTUGAL(0x383630),
        CANADA_FRENCH(0x383633),
        NORDIC(0x383635);

        private int value;

        Cpn(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Cpn getEnum(int value) {
            for (Cpn v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    // Disk Format Code (DFC)
    public enum Dfc {
        STL25("STL25.01", 25),
        STL30("STL30.01", 30);

        private String value;
        private int frameRate;

        Dfc(String value, int frameRate) {
            this.value = value;
            this.frameRate = frameRate;
        }

        public String getValue() {
            return this.value;
        }

        public int getFrameRate() {
            return this.frameRate;
        }

        public static Dfc getEnum(String value) {
            for (Dfc v : values()) {
                if (v.getValue().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    // Display Standard Code (DSC)
    public enum Dsc {
        UNDEFINED(0x20),
        OPEN_SUBTITLING(0x30),
        DSC_TELETEXT_LEVEL_1(0x31),
        DSC_TELETEXT_LEVEL_2(0x32);

        private int value;

        Dsc(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Dsc getEnum(int value) {
            for (Dsc v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    // Character Code Table (CCT)
    public enum Cct {
        LATIN_CYRILLIC(0x3031, Charset.forName("ISO-8859-5")),
        LATIN_ARABIC(0x3032, Charset.forName("ISO-8859-6")),
        LATIN_GREEK(0x3033, Charset.forName("ISO-8859-7")),
        LATIN_HEBREW(0x3034, Charset.forName("ISO-8859-8"));

        private int value;
        private Charset charset;

        Cct(int value, Charset charset) {
            this.value = value;
            this.charset = charset;
        }

        public int getValue() {
            return this.value;
        }

        public Charset getCharset() {
            return this.charset;
        }

        public static Cct getEnum(int value) {
            for (Cct v : values()) {
                if (v.getValue() == value) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

}
