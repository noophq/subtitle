/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.srt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.hotmart.subtitle.util.TimecodeBadlyFormattedException;

import fr.noop.subtitle.exception.InvalidTimeRangeException;
import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitlePlainText;
import fr.noop.subtitle.util.SubtitleTextLine;
import fr.noop.subtitle.util.SubtitleTimeCode;

/**
 * Created by clebeaupin on 21/09/15.
 */
public class SrtParser implements SubtitleParser {
    private enum CursorStatus {
        NONE,
        CUE_ID,
        CUE_TIMECODE,
        CUE_TEXT;
    }

    private String charset; // Charset of the input files

    public SrtParser(String charset) {
        this.charset = charset;
    }

    @Override
    public SrtObject parse(InputStream is) throws IOException, SubtitleParsingException, InvalidTimeRangeException {
    	return parse(is, true);
    }
    
    @Override
    public SrtObject parse(InputStream is, boolean strict) throws IOException, SubtitleParsingException, InvalidTimeRangeException {
        // Create srt object
        SrtObject srtObject = new SrtObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
        String textLine = "";
        CursorStatus cursorStatus = CursorStatus.NONE;
        SrtCue cue = null;
        int lineCount = 0;

        while ((textLine = br.readLine()) != null) {
        	lineCount++;
            textLine = textLine.trim();

            if (cursorStatus == CursorStatus.NONE) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // New cue
                cue = new SrtCue();

                // First textLine is the cue number
                try {
                    textLine = textLine.replace("\uFEFF","");//Remove Unicode BOM Character
                    Integer.parseInt(textLine);
                } catch (NumberFormatException e) {
                    throw new SubtitleParsingException(String.format(
                            "Unable to parse cue number: %s",
                            textLine), lineCount);
                }

                cue.setId(textLine);
                cursorStatus = CursorStatus.CUE_ID;
                continue;
            }

            // Second textLine defines the start and end time codes
            // 00:01:21,456 --> 00:01:23,417
            if (cursorStatus == CursorStatus.CUE_ID) {
                if (!textLine.substring(13, 16).equals("-->")) {
                	throw new TimecodeBadlyFormattedException(String.format(
                            "Timecode textLine is badly formated: %s", textLine), lineCount);
                }
                
                String startTime = textLine.substring(0, 12);
                String endTime = textLine.substring(17);
                
                if (startTime.length() != 12 || endTime.length() != 12) {
                	throw new TimecodeBadlyFormattedException(String.format(
                            "Timecode textLine is badly formated: %s", textLine), lineCount);
                }

                cue.setStartTime(this.parseTimeCode(startTime, lineCount));
                cue.setEndTime(this.parseTimeCode(endTime, lineCount));
                cursorStatus = CursorStatus.CUE_TIMECODE;
                continue;
            }

            // Following lines are the cue lines
            if (!textLine.isEmpty() && (
                    cursorStatus == CursorStatus.CUE_TIMECODE ||
                    cursorStatus ==  CursorStatus.CUE_TEXT)) {
                SubtitleTextLine line = new SubtitleTextLine();
                line.addText(new SubtitlePlainText(textLine));
                cue.addLine(line);
                cursorStatus = CursorStatus.CUE_TEXT;
                continue;
            }

            if (cursorStatus == CursorStatus.CUE_TEXT && textLine.isEmpty()) {
                // End of cue
                srtObject.addCue(cue);
                cue = null;
                cursorStatus = CursorStatus.NONE;
                continue;
            }

            throw new SubtitleParsingException(String.format("Unexpected line: %s", textLine), lineCount);
        }

        if (cue != null) {
            srtObject.addCue(cue);
        }

        return srtObject;
    }

    private SubtitleTimeCode parseTimeCode(String timeCodeString, int lineCount) throws SubtitleParsingException, InvalidTimeRangeException {
        try {
            int hour = Integer.parseInt(timeCodeString.substring(0, 2));
            int minute = Integer.parseInt(timeCodeString.substring(3, 5));
            int second = Integer.parseInt(timeCodeString.substring(6, 8));
            int millisecond = Integer.parseInt(timeCodeString.substring(9, 12));
            return new SubtitleTimeCode(hour, minute, second, millisecond);
        } catch (NumberFormatException e) {
            throw new SubtitleParsingException(String.format(
                    "Unable to parse time code: %s", timeCodeString), lineCount);
        }
    }
}
