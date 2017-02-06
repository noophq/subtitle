/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.base.BaseSubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttParser extends BaseSubtitleParser {
    // \uFEFF - BOM character
    private static final Pattern WEBVTT = Pattern.compile("^\uFEFF?WEBVTT(( |\t).*)?$");

    private static final String COMMENT_START = "NOTE";
    private static final String STYLE_START = "STYLE";
    private static final String REGION_START = "REGION";
    public static final String ARROW = "-->";


    private enum VttEvent {
        NONE,
        REGION,
        STYLE,
        NOTE,
        CUE_ID,
        CUE_TIMECODE,
        EMPTY_LINE,
        EOF
    }

    private Charset charset; // Charset of the input files
    private VttObject vttObject; // current VttObject

    private SubtitleReader reader; // the reader

    public VttParser(Charset charset) {
        this.charset = charset;
    }

    public int getLineNumber() {
        return (reader == null) ? 0 : reader.getLineNumber();
    }

    public int getColumn() {
        return (reader == null) ? 0 : reader.getColumn();
    }

    // unit tests only
    protected void setSource(SubtitleReader lnrd) {
        reader = lnrd;
    }


    /**
     * Positions the input right before the next event, and returns the kind of event found. Does not
     * consume any data from such event, if any.
     *
     * @return The kind of event found.
     */
    private VttEvent getNextEvent(String line, boolean cues) throws SubtitleParsingException {
        VttEvent foundEvent = VttEvent.NONE;

        if (line == null) {
            foundEvent = VttEvent.EOF;
        } else if (line.startsWith(STYLE_START)) {
            if (cues) {
                String msg = STYLE_START + " inside cues";
                notifyError(msg);
            }
            foundEvent = VttEvent.STYLE;
        } else if (line.startsWith(COMMENT_START)) {
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
    private StringBuilder readBlockLines() throws IOException {
        StringBuilder bld = new StringBuilder();
        String textLine;
        while ((textLine = reader.readLine()) != null) {
            textLine = textLine.replace('\u0000', '\uFFFD');
            if (textLine.trim().isEmpty()) {
                break;
            }
            if (bld.length() > 0) {
                bld.append("\n");
            }
            bld.append(textLine);
        }
        return bld;
    }


    @Override
    public VttObject parse(InputStream is, int subtitleOffset, int maxDuration, boolean strict) throws IOException, SubtitleParsingException {
        // reset state
        boolean inCues = false; // are we in cues part or in styles part?
        vttObject = new VttObject(); // Create vtt object
        VttCue currentVttCue = null;

        // Read each lines
        try (SubtitleReader lnrd = new SubtitleReader(is, this.charset)) {

            reader = lnrd;
            parseWebVTT();

            String textLine;
            while ((textLine = reader.readLine()) != null) {
                // All Vtt files start with WEBVTT
                textLine = textLine.replace('\u0000', '\uFFFD');
                VttEvent event = getNextEvent(textLine, inCues);

                currentVttCue = null;
                switch (event) {
                    case CUE_ID:
                        currentVttCue = new VttCue(this, vttObject);
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
                            currentVttCue = new VttCue(this, vttObject);
                        }
                        currentVttCue.parseCueHeader(textLine);

                        // End of cue
                        // Process multilines text in one time
                        // A class or a style can be applied for more than one line
                        currentVttCue.parseCueText(reader);
                        vttObject.addCue(currentVttCue);
                        break;
                    case NOTE:
                        VttNote vttNote = new VttNote(this);
                        parseNote(vttNote);
                        vttObject.addNote(vttNote);
                        break;
                    case REGION:
                        VttRegion vttRegion = new VttRegion(this);
                        parseRegion(vttRegion);
                        if (vttRegion.getId() == null) {
                            notifyError("Missing region id" );
                        }
                        else if (!vttObject.addRegion(vttRegion)) {
                            notifyError("Duplicated region id: " + vttRegion.getId() );
                        }
                        break;
                    case STYLE:
                        VttStyle vttStyle = new VttStyle(this, vttObject);
                        parseStyle(vttStyle);
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
        }

        return vttObject;
    }

    private void parseWebVTT() throws IOException, SubtitleParsingException {
        // first line must be WEBVTT ...
        String textLine = reader.readLine();
        if (textLine == null || !WEBVTT.matcher(textLine).matches()) {
            notifyError("Invalid WEBVTT header " + textLine);
            return;
        }

        // read lines up to two new lines
        readBlockLines();
    }

    private void parseNote(VttNote note) throws SubtitleParsingException, IOException {
        StringBuilder noteText = readBlockLines();
        note.parse(noteText);
    }

    private void parseStyle(VttStyle style) throws SubtitleParsingException, IOException {
        style.parse(reader);
    }

    private void parseRegion(VttRegion region) throws SubtitleParsingException, IOException {
        StringBuilder regionText = readBlockLines();
        region.parse(regionText);
    }



    /**
     * Parses a percentage string.
     *
     * @param s The percentage string.
     * @return The parsed value, where 1.0 represents 100%.
     * @throws NumberFormatException If the percentage could not be parsed.
     */
    public static float parsePercentage(String s) throws NumberFormatException {
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