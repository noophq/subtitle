/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.stl;

import fr.noop.subtitle.util.SubtitleTimeCode;

import java.util.Date;

/**
 * Created by clebeaupin on 21/09/15.
 */
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
            for(Cpn v : values())
                if(v.getValue() == value) return v;
            throw new IllegalArgumentException("Invalid code page number: " + value);
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
            for(Dfc v : values())
                if(v.getValue().equalsIgnoreCase(value)) return v;
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
            for(Dsc v : values())
                if(v.getValue() == value) return v;
            throw new IllegalArgumentException();
        }
    }

    // Character Code Table (CCT)
    public enum Cct {
        LATIN(0x3030, "ISO-6937-2"),
        LATIN_CYRILLIC(0x3031, "ISO-8859-5"),
        LATIN_ARABIC(0x3032, "ISO-8859-6"),
        LATIN_GREEK(0x3033, "ISO-8859-7"),
        LATIN_HEBREW(0x3034, "ISO-8859-8");

        private int value;
        private String charset;

        Cct(int value, String charset) {
            this.value = value;
            this.charset = charset;
        }

        public int getValue() {
            return this.value;
        }

        public String getCharset() {
            return this.charset;
        }

        public static Cct getEnum(int value) {
            for(Cct v : values())
                if(v.getValue() == value) return v;
            throw new IllegalArgumentException();
        }
    }



    public Cpn getCpn() {
        return this.cpn;
    }

    public void setCpn(Cpn cpn) {
        this.cpn = cpn;
    }

    public Dfc getDfc() {
        return this.dfc;
    }

    public void setDfc(Dfc dfc) {
        this.dfc = dfc;
    }

    public Dsc getDsc() {
        return this.dsc;
    }

    public void setDsc(Dsc dsc) {
        this.dsc = dsc;
    }

    public Cct getCct() {
        return this.cct;
    }

    public void setCct(Cct cct) {
        this.cct = cct;
    }

    public int getLc() {
        return this.lc;
    }

    public void setLc(int lc) {
        this.lc = lc;
    }

    public String getOpt() {
        return this.opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getOet() {
        return this.oet;
    }

    public void setOet(String oet) {
        this.oet = oet;
    }

    public String getTpt() {
        return this.tpt;
    }

    public void setTpt(String tpt) {
        this.tpt = tpt;
    }

    public String getTet() {
        return this.tet;
    }

    public void setTet(String tet) {
        this.tet = tet;
    }

    public String getTn() {
        return this.tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public String getTcd() {
        return this.tcd;
    }

    public void setTcd(String tcd) {
        this.tcd = tcd;
    }

    public String getSlr() {
        return this.slr;
    }

    public void setSlr(String slr) {
        this.slr = slr;
    }

    public Date getCd() {
        return this.cd;
    }

    public void setCd(Date cd) {
        this.cd = cd;
    }

    public Date getRd() {
        return this.rd;
    }

    public void setRd(Date rd) {
        this.rd = rd;
    }

    public int getRn() {
        return this.rn;
    }

    public void setRn(int rn) {
        this.rn = rn;
    }

    public int getTnb() {
        return this.tnb;
    }

    public void setTnb(int tnb) {
        this.tnb = tnb;
    }

    public int getTns() {
        return this.tns;
    }

    public void setTns(int tns) {
        this.tns = tns;
    }

    public int getTng() {
        return this.tng;
    }

    public void setTng(int tng) {
        this.tng = tng;
    }

    public int getMnc() {
        return this.mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getMnr() {
        return this.mnr;
    }

    public void setMnr(int mnr) {
        this.mnr = mnr;
    }

    public short getTcs() {
        return this.tcs;
    }

    public void setTcs(short tcs) {
        this.tcs = tcs;
    }

    public SubtitleTimeCode getTcp() {
        return this.tcp;
    }

    public void setTcp(SubtitleTimeCode tcp) {
        this.tcp = tcp;
    }

    public SubtitleTimeCode getTcf() {
        return this.tcf;
    }

    public void setTcf(SubtitleTimeCode tcf) {
        this.tcf = tcf;
    }

    public short getTnd() {
        return this.tnd;
    }

    public void setTnd(short tnd) {
        this.tnd = tnd;
    }

    public short getDsn() {
        return this.dsn;
    }

    public void setDsn(short dsn) {
        this.dsn = dsn;
    }

    public String getCo() {
        return this.co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getPub() {
        return this.pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public String getEn() {
        return this.en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getEcd() {
        return this.ecd;
    }

    public void setEcd(String ecd) {
        this.ecd = ecd;
    }

    public String getUda() {
        return this.uda;
    }

    public void setUda(String uda) {
        this.uda = uda;
    }
}
