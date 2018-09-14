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
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;

import java.io.IOException;

/**
 * Created by clebeaupin on 21/09/15.
 */
public class SrtParser extends BaseSubtitleParser {
    private enum SrtEvent {
        CUE_ID,
        CUE_TIMECODE,
        EMPTY_LINE
    }

    public SrtParser(ValidationReporter reporter, SubtitleReader reader) {
        super(reporter, reader);
    }

    /**
     * Positions the input right before the next event, and returns the kind of event found. Does not
     * consume any data from such event, if any.
     *
     * @return The kind of event found.
     */
    private SrtEvent getNextEvent(String line) {
        SrtEvent foundEvent;

        if (line.contains(SrtCue.ARROW)) {
            foundEvent = SrtEvent.CUE_TIMECODE;
        } else if (!line.trim().isEmpty()) {
            foundEvent = SrtEvent.CUE_ID;
        } else {
            foundEvent = SrtEvent.EMPTY_LINE;
        }

        return foundEvent;
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException {
        // Create srt object
        SrtObject srtObject = new SrtObject(); // current SrtObject

        String textLine;
        SrtCue cue = null;
        int lastCueNumber = 0;
        
        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.replace('\u0000', '\uFFFD');
            SrtEvent event = getNextEvent(textLine);

            switch (event) {
                case CUE_ID:
                    // First is cue number
                    // New cue
                    cue = new SrtCue(reporter, srtObject);
                    lastCueNumber = cue.parseCueId(textLine, lastCueNumber);
                    break;
                case CUE_TIMECODE:
                    // Second textLine defines the start and end time codes
                    // 00:01:21,456 --> 00:01:23,417
                    if (cue == null) {
                        notifyError("Cue timecode without ID");
                        cue = new SrtCue(reporter, srtObject);
                    }
                    if (cue.parseCueHeader(textLine, subtitleOffset)) {
                        cue.parseCueText(reader);
                        srtObject.addCue(cue);
                    }
                    cue = null;
                    break;
                case EMPTY_LINE:
                    break; // read next line
            }
        }

        if (cue != null) {
            notifyError("Cue ID without body");
        }

        return srtObject;
    }
}
