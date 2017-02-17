/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.srt;

import com.blackboard.collaborate.csl.validators.subtitle.base.BaseSubtitleParser;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitlePlainText;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTextLine;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 21/09/15.
 */
public class SrtParser extends BaseSubtitleParser {
    private enum CursorStatus {
        NONE,
        CUE_ID,
        CUE_TIMECODE,
        CUE_TEXT
    }

    static final String ARROW = "-->";
    private static final Pattern CUE_TIME_PATTERN = Pattern.compile("^\\s*(\\S+)\\s+" + ARROW + "\\s+(\\S+)(.*)?$");

    public SrtParser(ValidationReporter reporter, SubtitleReader reader) {
        super(reporter, reader);
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException {
        // Create srt object
        SrtObject srtObject = new SrtObject(); // current SrtObject

        String textLine;
        CursorStatus cursorStatus = CursorStatus.NONE;
        SrtCue cue = null;

        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.trim().replace('\u0000', '\uFFFD');

            if (cursorStatus == CursorStatus.NONE) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // New cue
                cue = new SrtCue();

                // First textLine is the cue number
                try {
                    Integer.parseInt(textLine);
                } catch (NumberFormatException e) {
                    notifyError("Unable to parse cue number: " + textLine);
                }

                cue.setId(textLine);
                cursorStatus = CursorStatus.CUE_ID;
                continue;
            }

            // Second textLine defines the start and end time codes
            // 00:01:21,456 --> 00:01:23,417
            if (cursorStatus == CursorStatus.CUE_ID) {
                Matcher m = CUE_TIME_PATTERN.matcher(textLine);
                if (!m.matches()) {
                    notifyError("Timecode '" + textLine + "' is badly formated");
                }
                else {
                    cue.setStartTime(this.parseTimeCode(textLine.substring(0, 12), subtitleOffset));
                    cue.setEndTime(this.parseTimeCode(textLine.substring(17), subtitleOffset));
                }
                cursorStatus = CursorStatus.CUE_TIMECODE;
                continue;
            }

            // Following lines are the cue lines
            if (!textLine.isEmpty() && (
                    cursorStatus == CursorStatus.CUE_TIMECODE || cursorStatus == CursorStatus.CUE_TEXT)) {
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

            notifyError("Unexpected line: " + textLine);
        }

        if (cue != null) {
            srtObject.addCue(cue);
        }

        return srtObject;
    }

    // duplicity in VTT
    protected SubtitleTimeCode parseTimeCode(String timeCodeString, int subtitleOffset) {
        long value = 0;
        String[] parts = timeCodeString.split("\\.", 2);
        if (parts.length > 2) {
            notifyError("Invalid time value: " + timeCodeString);
            return null;
        }
        String[] subparts = parts[0].split(":");
        if (subparts.length > 3) {
            notifyError("Invalid time value: " + timeCodeString);
            return null;
        }
        try {
            for (String subpart : subparts) {
                value = value * 60 + Long.parseLong(subpart);
            }

            long stamp = value * 1000 + subtitleOffset;
            if (parts.length > 1) {
                stamp += Long.parseLong(parts[1]);
            }

            return new SubtitleTimeCode(stamp);

        } catch (NumberFormatException e) {
            notifyError("Invalid time format: " + timeCodeString);
        } catch (IllegalArgumentException e) {
            notifyError("Invalid time value: " + timeCodeString);
        }
        return null;
    }
}
