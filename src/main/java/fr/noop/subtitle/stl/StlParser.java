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

import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleTimeCode;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by clebeaupin on 21/09/15.
 */
public class StlParser implements SubtitleParser {
    public StlParser() {
    }

    @Override
    public StlObject parse(InputStream is) throws SubtitleParsingException {
        return parse(is, true);
    }

    @Override
    public StlObject parse(InputStream is, boolean strict) throws SubtitleParsingException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);

        // Create STL subtitle
        StlObject stl;

        try {
            // Read GSI block
            StlGsi gsi = this.readGsi(dis);
            stl = new StlObject(gsi);
        } catch (IOException e) {
            throw new SubtitleParsingException("Unable to parse Gsi block");
        }

        // Iterate over all TTI blocks and parse them
        int subtitleIndex = 0;

        while (subtitleIndex++ < stl.getGsi().getTnb()) {
            StlTti tti;

            try {
                tti = this.readTti(dis, stl.getGsi());
            } catch (IOException e) {
                throw new SubtitleParsingException("Unable to parse tti block");
            }

            stl.addTti(tti);
        }

        return stl;
    }

    private Date readDate(String dateString) {
        DateFormat df = new SimpleDateFormat("yyMMdd");

        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            System.out.printf("Warning :: Unable to parse Date : '%s', using default date\n", dateString);
            return new Date(0);
        }
    }

    private SubtitleTimeCode readTimeCode(String timeCodeString, float frameRate) throws IOException {
        if (timeCodeString.equals("")) {
            return new SubtitleTimeCode(0, 0, 0, 0);
        }
        InnerTime t = new InnerTime();

        t.hour = Integer.parseInt(timeCodeString.substring(0, 2));
        t.minute = Integer.parseInt(timeCodeString.substring(2, 4));
        t.second = Integer.parseInt(timeCodeString.substring(4, 6));
        int frame = Integer.parseInt(timeCodeString.substring(6, 8));
        
        // Frame duration in milliseconds
        float frameDuration = (1000 / frameRate);
        t.millisecond = Math.round(frame * frameDuration);
        // and some STL have 1-25 encoded frame...
        InnerTime fixedT = fixTime(t);
        // Build time code
        return new SubtitleTimeCode(fixedT.hour, fixedT.minute, fixedT.second, fixedT.millisecond);
    }

    class InnerTime {
        int hour;
        int minute;
        int second;
        int millisecond;

        public String toString() {
            return String.format("%02d:%02d:%02d.%03d", this.hour, this.minute, this.second, this.millisecond);
        }
    }

    private InnerTime fixTime(InnerTime t) {
        if (t.millisecond >= 1000) {
            int newHours = t.hour;
            int newMinutes = t.minute;
            int newMillis = t.millisecond - 1000;
            int newSeconds = t.second + 1;
            if (newSeconds >= 60) {
                newSeconds -= 60;
                newMinutes += 1;
            }
            if (newMinutes >= 60) {
                newMinutes -= 0;
                newHours += 1;
            }
            InnerTime newt = new InnerTime();
            newt.hour = newHours;
            newt.second = newSeconds;
            newt.minute = newMinutes;
            newt.second = newSeconds;
            newt.millisecond = newMillis;
            return newt;
        } else {
            return t;
        }
    }

    private SubtitleTimeCode readTimeCode(DataInputStream dis, float frameRate) throws IOException {
        InnerTime t = new InnerTime();

        t.hour = dis.readUnsignedByte();
        t.minute = dis.readUnsignedByte();
        t.second = dis.readUnsignedByte();
        int frame = dis.readUnsignedByte();
        // Frame duration in milliseconds
        float frameDuration = (1000 / frameRate);
        t.millisecond = Math.round(frame * frameDuration);
        // and some STL have 1-25 encoded frame...
        InnerTime fixedT = fixTime(t);

        // Build time code
        SubtitleTimeCode st = new SubtitleTimeCode(fixedT.hour, fixedT.minute, fixedT.second, fixedT.millisecond);
        return st;
    }

    private String readString(DataInputStream dis, int length, String charset) throws IOException {
        byte[] bytes = new byte[length];
        dis.readFully(bytes, 0, length);

        // Remove spaces at start and end of the string
        return StringUtils.strip(new String(bytes, charset));
    }

    private String readString(DataInputStream dis, int length) throws IOException {
        byte[] bytes = new byte[length];
        dis.readFully(bytes, 0, length);

        // Remove spaces at start and end of the string
        return StringUtils.strip(new String(bytes));
    }

    private StlGsi readGsi(DataInputStream dis) throws IOException {
        // Read and extract metadata from GSI block
        // GSI block is 1024 bytes long
        StlGsi gsi = new StlGsi();

        // Read Code Page Number (CPN)
        byte[] cpnBytes = new byte[3];
        dis.readFully(cpnBytes, 0, 3);
        int cpn = cpnBytes[0] << 16 | cpnBytes[1] << 8 | cpnBytes[2];
        gsi.setCpn(StlGsi.Cpn.getEnum(cpn));

        // Read Disk Format Code (DFC)
        gsi.setDfc(StlGsi.Dfc.getEnum(this.readString(dis, 8)));

        // Read Display Standard Code (DSC)
        gsi.setDsc(StlGsi.Dsc.getEnum(dis.readUnsignedByte()));

        // Read Character Code Table number (CCT)
        gsi.setCct(StlGsi.Cct.getEnum(dis.readShort()));

        // Read Character Language Code (LC)
        gsi.setLc(LanguageCode.Lc.getEnum(dis.readShort()));

        // Read Original Programme Title (OPT)
        gsi.setOpt(this.readString(dis, 32));

        // Read Original Programme Title (OET)
        gsi.setOet(this.readString(dis, 32));

        // Read Translated Programme Title (TPT)
        gsi.setTpt(this.readString(dis, 32));

        // Read translated Episode Title (TET)
        gsi.setTet(this.readString(dis, 32));

        // Read Translator's Name (TN)
        gsi.setTn(this.readString(dis, 32));

        // Read Translator's Contact Details (TCD)
        gsi.setTcd(this.readString(dis, 32));

        // Read Subtitle List Reference Code (SLR)
        gsi.setSlr(this.readString(dis, 16));

        // Read Creation Date (CD)
        gsi.setCd(this.readDate(this.readString(dis, 6)));

        // Read Revision Date (RD)
        gsi.setRd(this.readDate(this.readString(dis, 6)));

        // Read Revision number RN
        gsi.setRn(Short.reverseBytes(dis.readShort()));

        // Read Total Number of Text and Timing Information (TTI) blocks (TNB)
        gsi.setTnb(Integer.parseInt(this.readString(dis, 5)));

        // Read Total Number of Subtitles (TNS)
        gsi.setTns(Integer.parseInt(this.readString(dis, 5)));

        // Read Total Number of Subtitle Groups (TNG)
        dis.skipBytes(3);

        // Read Maximum Number of Displayable Characters in any text row (MNC)
        gsi.setMnc(Integer.parseInt(this.readString(dis, 2)));

        // Read Maximum Number of Displayable Rows (MNR)
        gsi.setMnr(Integer.parseInt(this.readString(dis, 2)));
        // Read Time Code: Status (TCS)
        gsi.setTcs(StlGsi.Tcs.getEnum(dis.readUnsignedByte()));

        // Read Time Code: Start-of-Programme (TCP)
        try {
            gsi.setTcp(this.readTimeCode(this.readString(dis, 8), gsi.getDfc().getFrameRate()));
        } catch (NumberFormatException e) {
            System.out.printf("Can't read timecode  with message : %s\n", e.getMessage());
            System.out.println("Setting 0000000 as timecode");
            gsi.setTcp(new SubtitleTimeCode(0));
        }
        
        // Read Time Code: First In-Cue (TCF)
        try {
            gsi.setTcf(this.readTimeCode(this.readString(dis, 8), gsi.getDfc().getFrameRate()));
        } catch (NumberFormatException e) {
            System.out.printf("Can't read timecode  with message : %s\n", e.getMessage());
            System.out.println("Setting 0000000 as timecode");
            gsi.setTcf(new SubtitleTimeCode(0));
        }

        // Read Total Number of Disks (TND)
        gsi.setTnd((short) dis.readUnsignedByte());

        // Read Disk Sequence Number (DSN)
        gsi.setDsn((short) dis.readUnsignedByte());

        // Read Country of Origin (CO)
        gsi.setCo(this.readString(dis, 3));

        // Read Publisher (PUB)
        gsi.setPub(this.readString(dis, 32));

        // Read Editor's Name (EN)
        gsi.setEn(this.readString(dis, 32));

        // Read Editor's Contact Details (ECD)
        gsi.setEcd(this.readString(dis, 32));

        // Spare Bytes
        dis.skipBytes(75);

        // Read User-Defined Area (UDA)
        gsi.setUda(this.readString(dis, 576));
        return gsi;
    }

    private StlTti readTti(DataInputStream dis, StlGsi gsi) throws IOException {
        // Get charset from gsi
        String charset = gsi.getCct().getCharset();

        // Get frame rate from gsi
        float frameRate = gsi.getDfc().getFrameRate();

        // Read and extract metadata from TTI block
        // Each TTI block is 128 bytes long
        StlTti tti = new StlTti();

        // Read Subtitle Group Number (SGN)
        tti.setSgn((short) dis.readUnsignedByte());

        // Read Subtitle Number (SN)
        tti.setSn(Short.reverseBytes(dis.readShort()));

        // Read Extension Block Number (EBN)
        tti.setEbn((short) dis.readUnsignedByte());

        // Read Cumulative Status (CS)
        tti.setCs((short) dis.readUnsignedByte());

        // Read Time Code In (TCI)
        tti.setTci(this.readTimeCode(dis, frameRate));

        // Read Time Code Out (TCO)
        tti.setTco(this.readTimeCode(dis, frameRate));

        // Read Vertical Position (VP)
        tti.setVp((short) dis.readUnsignedByte());

        // Read Justification Code (JC)
        tti.setJc(StlTti.Jc.getEnum(dis.readUnsignedByte()));

        // Read Comment Flag (CF)
        tti.setCf((short) dis.readUnsignedByte());

        // Read TextField (TF)
        byte[] tfBytes = new byte[112];
        dis.readFully(tfBytes, 0, 112);
        tti.setTf(new String(tfBytes, charset));

        // TTI is fully parsed
        return tti;
    }
}
