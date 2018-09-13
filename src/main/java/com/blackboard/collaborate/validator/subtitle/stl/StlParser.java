/*
 * Title: StlParser
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

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by clebeaupin on 21/09/15.
 */
public class StlParser extends BaseSubtitleParser {

    static class BinarySubtitleReader extends DataInputStream {
        public BinarySubtitleReader(InputStream in) {
            super(in);
        }
    }

    private BinarySubtitleReader breader;

    public StlParser(ValidationReporter reporter, SubtitleReader reader) {
        super(reporter, null); // TODO: STL format would need binary reader
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException {
        // Create STL subtitle
        StlObject stl;


        // Read GSI block
        StlGsi gsi = this.readGsi(breader, subtitleOffset);
        stl = new StlObject(gsi);

        // Iterate over all TTI blocks and parse them
        int subtitleIndex = 0;

        while (subtitleIndex++ < stl.getGsi().getTnb()) {
            StlTti tti = this.readTti(breader, stl.getGsi());
            stl.addTti(tti);
        }

        return stl;
    }

    private Date readDate(String dateString) throws IOException {
        DateFormat df = new SimpleDateFormat("yyMMdd");

        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            throw new IOException("Unable to parse date");
        }
    }

    private SubtitleTimeCode readTimeCode(String timeCodeString, int frameRate, int subtitleOffset) {
        int hour = Integer.parseInt(timeCodeString.substring(0, 2));
        int minute = Integer.parseInt(timeCodeString.substring(2, 4));
        int second = Integer.parseInt(timeCodeString.substring(4, 6));
        int frame = Integer.parseInt(timeCodeString.substring(6, 8));

        // Frame duration in milliseconds
        int frameDuration = (1000 / frameRate);

        // Build time code
        return new SubtitleTimeCode(hour, minute, second, frame * frameDuration, subtitleOffset);
    }

    private SubtitleTimeCode readTimeCode(DataInputStream dis, int frameRate) throws IOException {
        int hour = dis.readUnsignedByte();
        int minute = dis.readUnsignedByte();
        int second = dis.readUnsignedByte();
        int frame = dis.readUnsignedByte();

        // Frame duration in milliseconds
        int frameDuration = (1000/frameRate);

        // Build time code
        return new SubtitleTimeCode(hour, minute, second, frame*frameDuration);
    }

    private String readString(DataInputStream dis, int length) throws IOException {
        byte[] bytes = new byte[length];
        dis.readFully(bytes, 0, length);

        // Remove spaces at start and end of the string
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private StlGsi readGsi(DataInputStream dis, int subtitleOffset) throws IOException {
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
        gsi.setCct(StlGsi.Cct.getEnum(Short.reverseBytes(dis.readShort())));

        // Read Character Language Code (LC)
        gsi.setLc(Short.reverseBytes(dis.readShort()));

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
        gsi.setTcs((short) dis.readUnsignedByte());

        // Read Time Code: Start-of-Programme (TCP)
        gsi.setTcp(this.readTimeCode(this.readString(dis, 8), gsi.getDfc().getFrameRate(), subtitleOffset));

        // Read Time Code: First In-Cue (TCF)
        gsi.setTcf(this.readTimeCode(this.readString(dis, 8), gsi.getDfc().getFrameRate(), subtitleOffset));

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
        Charset charset = gsi.getCct().getCharset();

        // Get frame rate from gsi
        int frameRate = gsi.getDfc().getFrameRate();

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
