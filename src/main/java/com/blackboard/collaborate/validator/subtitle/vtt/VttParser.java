/*
 * Title: VttParser
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttParser extends BaseSubtitleParser {
    static final String WEBVTT_TAG = "WEBVTT";
    // \uFEFF - BOM character
    private static final Pattern WEBVTT = Pattern.compile("^\uFEFF?" + WEBVTT_TAG + "(( |\t).*)?$");

    static final String NOTE_START = "NOTE";
    static final String STYLE_START = "STYLE";
    static final String REGION_START = "REGION";
    static final String ARROW = "-->";


    private enum VttEvent {
        REGION,
        STYLE,
        NOTE,
        CUE_ID,
        CUE_TIMECODE,
        EMPTY_LINE
    }

    public VttParser(ValidationReporter reporter, SubtitleReader reader) {
        super(reporter, reader);
    }

    /**
     * Positions the input right before the next event, and returns the kind of event found. Does not
     * consume any data from such event, if any.
     *
     * @return The kind of event found.
     */
    private VttEvent getNextEvent(String line, boolean cues) {
        VttEvent foundEvent;

        if (line.startsWith(STYLE_START)) {
            if (cues) {
                String msg = STYLE_START + " inside cues";
                notifyError(msg);
            }
            foundEvent = VttEvent.STYLE;
        } else if (line.startsWith(NOTE_START)) {
            foundEvent = VttEvent.NOTE;
        } else if (line.startsWith(REGION_START)) {
            if (cues) {
                String msg = REGION_START + " inside cues";
                notifyError(msg);
            }
            foundEvent = VttEvent.REGION;
        } else if (line.contains(ARROW)) {
            foundEvent = VttEvent.CUE_TIMECODE;
        } else if (!line.trim().isEmpty()) {
            foundEvent = VttEvent.CUE_ID;
        } else {
            foundEvent = VttEvent.EMPTY_LINE;
        }

        return foundEvent;
    }

    /**
     * Read and record lines until two newlines are detected
     * @return A StringBuilder containing all the block lines
     */
    private StringBuilder readBlockLines(StringBuilder bld) throws IOException {
        String textLine;
        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.replace('\u0000', '\uFFFD');
            if (textLine.trim().isEmpty()) {
                break;
            }
            bld.append(textLine);
            bld.append("\n");
        }
        return bld;
    }


    @Override
    public VttObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException {
        // reset state
        boolean inCues = false; // are we in cues part or in styles part?
        VttObject vttObject = new VttObject(); // Create vtt object
        VttCue currentVttCue;

        parseWebVTT();

        String textLine;
        while ((textLine = reader.readLine()) != null) {
            // All Vtt files start with WEBVTT
            textLine = textLine.replace('\u0000', '\uFFFD');
            VttEvent event = getNextEvent(textLine, inCues);

            currentVttCue = null;
            switch (event) {
                case CUE_ID:
                    currentVttCue = new VttCue(reporter, vttObject);
                    currentVttCue.parseCueId(textLine); // ???
                    if ((textLine = reader.readLine()) == null) {
                        break;
                    }
                    textLine = textLine.replace('\u0000', '\uFFFD');
                    // $FALLTHROUGH
                case CUE_TIMECODE:
                    // textLine defines the start and end time codes
                    // 00:01:21.456 --> 00:01:23.417
                    inCues = true;
                    if (currentVttCue == null) {
                        currentVttCue = new VttCue(reporter, vttObject);
                    }
                    if (currentVttCue.parseCueHeader(textLine)) {
                        // End of cue
                        // Process multi-line text in one time
                        // A class or a style can be applied for more than one line
                        currentVttCue.parseCueText(reader);
                        vttObject.addCue(currentVttCue);
                    }
                    break;
                case NOTE:
                    VttNote vttNote = new VttNote(reporter);
                    parseNote(vttNote, textLine);
                    vttObject.addNote(vttNote);
                    break;
                case REGION:
                    VttRegion vttRegion = new VttRegion(reporter);
                    parseRegion(vttRegion, textLine);
                    if (vttRegion.getId() == null) {
                        notifyError("Missing region id" );
                    }
                    else if (!vttObject.addRegion(vttRegion)) {
                        notifyError("Duplicated region id: " + vttRegion.getId() );
                    }
                    break;
                case STYLE:
                    VttStyle vttStyle = new VttStyle(reporter, vttObject);
                    parseStyle(vttStyle, textLine);
                    vttObject.addStyles(vttStyle);
                    break;
                case EMPTY_LINE:
                    // nothing to do
                    break;
                default:
                    notifyError("Unexpected block name: " + textLine);
                    break;
            }
        }

        return vttObject;
    }

    private void parseWebVTT() throws IOException {
        // first line must be WEBVTT ...
        String textLine = reader.readLine();
        if (textLine == null || !WEBVTT.matcher(textLine).matches()) {
            notifyError("Invalid WEBVTT header " + textLine);
            return;
        }

        // read lines up to two new lines
        readBlockLines(new StringBuilder());
    }

    private void parseNote(VttNote note, String firstLine) throws IOException {
        StringBuilder noteText = new StringBuilder(firstLine);
        readBlockLines(noteText);
        note.parse(noteText);
    }

    private void parseStyle(VttStyle style, String firstLine) throws IOException {
        if (firstLine.trim().length() > VttParser.STYLE_START.length()) {
            notifyError("CSS Style does not start at new line");
        }
        style.parse(reader);
    }

    private void parseRegion(VttRegion region, String firstLine) throws IOException {
        StringBuilder regionText = new StringBuilder(firstLine);
        readBlockLines(regionText);
        region.parse(regionText);
    }

    /**
     * Parses a percentage string.
     *
     * @param s The percentage string.
     * @return The parsed value, where 1.0 represents 100%.
     * @throws NumberFormatException If the percentage could not be parsed.
     */
    static float parsePercentage(String s) throws NumberFormatException {
        if (!s.endsWith("%")) {
            throw new NumberFormatException("Percentage must end with %: " + s);
        }
        float f = Float.parseFloat(s.substring(0, s.length() - 1)) / 100f;
        if (f < 0f || f > 1f) {
            throw new NumberFormatException("Percentage must be within 0..100: " + s);
        }
        return f;
    }

}