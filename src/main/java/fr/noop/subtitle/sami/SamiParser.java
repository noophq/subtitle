/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.sami;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitlePlainText;
import fr.noop.subtitle.util.SubtitleTextLine;
import fr.noop.subtitle.util.SubtitleTimeCode;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiParser implements SubtitleParser {
    private enum CursorStatus {
        NONE,
        BODY_START,
        BODY_END,
        CUE_TIMECODE,
        CUE_TEXT;
    }

    private String charset; // Charset of the input files

    public SamiParser(String charset) {
        this.charset = charset;
    }


    @Override
    public SamiObject parse(InputStream is) throws IOException, SubtitleParsingException {
    	return parse(is, true);
    }
    
    @Override
    public SamiObject parse(InputStream is, boolean strict) throws IOException, SubtitleParsingException {
        // Create SAMI object
        SamiObject samiObject = new SamiObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
        String textLine = "";
        CursorStatus cursorStatus = CursorStatus.NONE;
        SamiCue cue = null;
        SamiCue previousCue = null;

        while ((textLine = br.readLine()) != null) {
            textLine = textLine.trim();
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
                    throw new SubtitleParsingException(String.format(
                            "Unexpected time code: %s", textLine));
                }
            }

            if ((cursorStatus == CursorStatus.BODY_START) ||
                    (cursorStatus == CursorStatus.CUE_TEXT) && lcTextLine.startsWith("<sync")) {
                // Get start time
                String text = textLine.substring(5).trim();

                if (!text.toLowerCase().startsWith("start=")) {
                    throw new SubtitleParsingException(String.format(
                            "Unexpected time code: %s", textLine));
                }

                // Make sure this is an integer
                String startTime = text.substring(6, text.length() - 1).trim();
                long time;

                try {
                    time = Long.valueOf(startTime);
                } catch (NumberFormatException e) {
                    throw new SubtitleParsingException(String.format(
                            "Unable to parse start time: %s",
                            textLine));
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

            throw new SubtitleParsingException(String.format(
                    "Unexpected line: %s", textLine));
        }

        // This is the end
        // Set end time for the last cue
        if (previousCue != null) {
            // Last cue duration is 2s
            previousCue.setEndTime(new SubtitleTimeCode(previousCue.getStartTime().getTime() + 2000));
        }

        return samiObject;
    }
}
