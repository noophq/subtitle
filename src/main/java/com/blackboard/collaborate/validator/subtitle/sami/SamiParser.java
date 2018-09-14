/*
 * Title: SamiParser
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.sami;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitlePlainText;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTextLine;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;

import java.io.IOException;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiParser extends BaseSubtitleParser {
    private enum CursorStatus {
        NONE,
        BODY_START,
        BODY_END,
        CUE_TIMECODE,
        CUE_TEXT
    }

    public SamiParser(ValidationReporter reporter, SubtitleReader reader) {
        super(reporter, reader);
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException {
        // Create SAMI object
        SamiObject samiObject = new SamiObject();

        // Read each lines
        String textLine;
        CursorStatus cursorStatus = CursorStatus.NONE;
        SamiCue cue = null;
        SamiCue previousCue = null;

        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.trim().replace('\u0000', '\uFFFD');
            // Lower case text line
            String lcTextLine = textLine.toLowerCase();

            if (lcTextLine.startsWith("</body>") || cursorStatus == CursorStatus.BODY_END) {
                cursorStatus = CursorStatus.BODY_END;
                continue;
            }

            if (cursorStatus == CursorStatus.NONE) {
                if (!lcTextLine.startsWith("<body>")) {
                    continue;
                }

                cursorStatus = CursorStatus.BODY_START;
                continue;
            }

            if (cursorStatus == CursorStatus.BODY_START) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // The next element after the body element is always the sync element
                if (!lcTextLine.startsWith("<sync")) {
                    notifyError("Unexpected time code: " + textLine);
                }
            }

            if ((cursorStatus == CursorStatus.BODY_START) ||
                    (cursorStatus == CursorStatus.CUE_TEXT) && lcTextLine.startsWith("<sync")) {
                // Get start time
                String text = textLine.substring(5).trim();

                if (!text.toLowerCase().startsWith("start=")) {
                    notifyError("Unexpected time code: " + textLine);
                }

                // Make sure this is an integer
                String startTime = text.substring(6, text.length() - 1).trim();
                long time = 0;

                try {
                    time = Long.parseLong(startTime) + subtitleOffset;
                } catch (NumberFormatException e) {
                    notifyError("Unable to parse start time: " + textLine);
                }

                // New cue
                cue = new SamiCue();
                cue.setStartTime(new SubtitleTimeCode(time));

                // Set end time for previous cue
                if (previousCue != null) {
                    previousCue.setEndTime(new SubtitleTimeCode(time));
                }

                samiObject.addCue(cue);
                previousCue = cue;
                cursorStatus = CursorStatus.CUE_TIMECODE;
                continue;
            }

            if (cursorStatus == CursorStatus.CUE_TIMECODE || cursorStatus == CursorStatus.CUE_TEXT) {
                // Remove <P> and </P> information
                String text = textLine;

                // Remove p start tag
                if (lcTextLine.startsWith("<p")) {
                    text = text.substring(text.indexOf(">")+1);
                }

                // Remove p end tag
                if (lcTextLine.endsWith("</p>")) {
                    text = text.substring(0, text.length()-4);
                }

                // Add new text line
                SubtitleTextLine line = new SubtitleTextLine();
                line.addText(new SubtitlePlainText(text));
                cue.addLine(line);
                cursorStatus = CursorStatus.CUE_TEXT;
                continue;
            }

            notifyError("Unexpected line: " + textLine);
        }

        // This is the end
        // Set end time for the last cue
        if (previousCue != null) {
            // Last cue duration is 2s
            previousCue.setEndTime(new SubtitleTimeCode(previousCue.getStartTime().getTime() + 2000 + subtitleOffset));
        }

        return samiObject;
    }
}
