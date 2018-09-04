/*
 * Title: SrtParser
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.srt;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitlePlainText;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTextLine;

import java.io.IOException;

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
        int lastCueNumber = 0;

        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.replace('\u0000', '\uFFFD');

            if (cursorStatus == CursorStatus.NONE) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // New cue
                cue = new SrtCue(reporter);
                lastCueNumber = cue.parseCueId(textLine, lastCueNumber);
                cursorStatus = CursorStatus.CUE_ID;
                continue;
            }

            // Second textLine defines the start and end time codes
            // 00:01:21,456 --> 00:01:23,417
            if (cursorStatus == CursorStatus.CUE_ID) {
                if (cue.parseCueHeader(textLine, subtitleOffset)) {
                    cursorStatus = CursorStatus.CUE_TIMECODE;
                }
                continue;
            }

            // Following lines are the cue lines
            if (!textLine.isEmpty() && (cursorStatus == CursorStatus.CUE_TIMECODE || cursorStatus == CursorStatus.CUE_TEXT)) {
                SubtitleTextLine line = new SubtitleTextLine();
                line.addText(new SubtitlePlainText(textLine));
                cue.addLine(line);
                cursorStatus = CursorStatus.CUE_TEXT;
                continue;
            }

            if (cursorStatus == CursorStatus.CUE_TEXT) {
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
}
