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

import java.io.LineNumberReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.noop.subtitle.base.BaseSubtitleParser;
import fr.noop.subtitle.base.CueElemData;
import fr.noop.subtitle.base.CuePlainData;
import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.util.SubtitleStyle;

import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleTimeCode;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttParser extends BaseSubtitleParser {
    private static final Pattern WEBVTT = Pattern.compile("^\uFEFF?WEBVTT(( |\t).*)?$");

    private static final String COMMENT_START = "NOTE";
    private static final String STYLE_START = "STYLE";
    private static final String REGION_START = "REGION";
    private static final String ARROW = "-->";

    private static final Pattern CUE_TIME_PATTERN = Pattern.compile("^\\s*(\\S+)\\s+" + ARROW + "\\s+(\\S+)(.*)?$");
    private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    private static final Pattern REGION_PATTERN = Pattern.compile("(\\S+):(\\S+)");

    private enum CursorStatus {
        NONE,
        REGION,
        STYLE,
        NOTE,
        CUE_ID,
        CUE_TIMECODE,
        CUE_TEXT,
        EMPTY_LINE,
        EOF
    }

    private enum TagStatus {
        NONE,
        OPEN,
        CLOSE
    }

    private Charset charset; // Charset of the input files
    private boolean inCues = false; // are we in cues part or in styles part?
    private VttObject vttObject; // current VttObject

    private int maxDuration = Integer.MAX_VALUE;
    private int subtitleOffset = 0;
    private LineNumberReader br;

    public VttParser(Charset charset) {
        this.charset = charset;
    }

    public int getLineNumber() {
        return (br == null) ? 0 : br.getLineNumber();
    }

    public int getColumn() {
        return 0;
    }

    // unit tests only
    protected void setSource(LineNumberReader lnrd) {
        br = lnrd;
    }


    /**
     * Positions the input right before the next event, and returns the kind of event found. Does not
     * consume any data from such event, if any.
     *
     * @return The kind of event found.
     */
    private CursorStatus getNextEvent(String line, boolean cues) throws SubtitleParsingException {
        CursorStatus foundEvent = CursorStatus.NONE;

        if (line == null) {
            foundEvent = CursorStatus.EOF;
        } else if (line.startsWith(STYLE_START)) {
            if (cues) {
                String msg = STYLE_START + " inside cues";
                notifyError(msg);
            }
            foundEvent = CursorStatus.STYLE;
        } else if (line.startsWith(COMMENT_START)) {
            foundEvent = CursorStatus.NOTE;
        } else if (line.startsWith(REGION_START)) {
            if (cues) {
                String msg = REGION_START + " inside cues";
                notifyError(msg);
            }
            foundEvent = CursorStatus.REGION;
        } else if (line.contains(ARROW)) {
            foundEvent = CursorStatus.CUE_TIMECODE;
        } else if (!line.trim().isEmpty()) {
            foundEvent = CursorStatus.CUE_ID;
        } else {
            foundEvent = CursorStatus.EMPTY_LINE;
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
        while ((textLine = br.readLine()) != null) {
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
        inCues = false;
        vttObject = new VttObject(); // Create vtt object
        boolean shouldIgnoreCurrentCue = false; // ???

        // Read each lines
        try (LineNumberReader lnrd = new LineNumberReader(new InputStreamReader(is, this.charset))) {

            br = lnrd;
            parseWebVTT();

            String textLine;
            while ((textLine = br.readLine()) != null) {
                // All Vtt files start with WEBVTT

                CursorStatus event = getNextEvent(textLine, inCues);

                VttCue cue = null;
                switch (event) {
                    case CUE_ID:
                        cue = new VttCue();
                        parseCueId(cue, textLine); // ???
                        if ((textLine = br.readLine()) == null) {
                            break;
                        }
                        // $FALLTHROUGH
                    case CUE_TIMECODE:
                        // textLine defines the start and end time codes
                        // 00:01:21.456 --> 00:01:23.417
                        inCues = true;
                        if (cue == null) {
                            cue = new VttCue();
                        }
                        parseCueHeader(cue, textLine);

                        // End of cue
                        // Process multilines text in one time
                        // A class or a style can be applied for more than one line
                        CueTreeNode node = parseCueTextTree();
                        cue.setTree(node);
                        vttObject.addCue(cue);
                        break;
                    case NOTE:
                        VttNote vttNote = new VttNote();
                        parseNote(vttNote);
                        //vttObject.getNotes().add(vttNote);
                        break;
                    case REGION:
                        VttRegion vttRegion = new VttRegion();
                        parseRegion(vttRegion);
                        if (vttRegion.getId() == null) {
                            notifyError("Missing region id" );
                        }
                        else if (!vttObject.addRegion(vttRegion)) {
                            notifyError("Duplicated region id: " + vttRegion.getId() );
                        }
                        break;
                    case STYLE:
                        VttStyle vttStyle = new VttStyle();
                        parseStyle(vttStyle);
                        vttObject.getStyles().add(vttStyle);
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
        String textLine = br.readLine();
        if (textLine == null || !WEBVTT.matcher(textLine).matches()) {
            notifyError("Invalid WEBVTT header " + textLine);
        }

        // new line
        textLine = br.readLine();
        if (textLine != null) {
            CursorStatus event = getNextEvent(textLine, inCues);
            if (event != CursorStatus.EMPTY_LINE) {
                notifyError("Empty line expected after WEBVTT header");
            }
        }
    }

    private void parseNote(VttNote note) throws SubtitleParsingException, IOException {
        StringBuilder cueText = readBlockLines();
        if (cueText.indexOf(ARROW) >= 0) {
            notifyError("'" + ARROW + "' found inside comment block");
        }
    }

    private void parseStyle(VttStyle style) throws SubtitleParsingException, IOException {
        StringBuilder cueText = readBlockLines();
        // TODO
    }

    private void parseRegion(VttRegion region) throws SubtitleParsingException, IOException {
        StringBuilder regionText = readBlockLines();

        Matcher m = REGION_PATTERN.matcher(regionText);
        while (m.find()) {
            String name = m.group(1);
            String value = m.group(2);

            switch (name) {
                case "id":
                    if (value.contains(ARROW)) {
                        notifyWarning("Invalid region " + name + ": " + value);
                    }
                    region.setId(value);
                    break;
                case "width":
                    try {
                        parsePercentage(value);
                    } catch (NumberFormatException e) {
                        notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "lines":
                    try {
                        int l = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "viewportanchor":
                case "regionanchor":
                    String parts[] = value.split(",");
                    if (parts.length == 2) {
                        try {
                            parsePercentage(parts[0]);
                            parsePercentage(parts[1]);
                        } catch (NumberFormatException e) {
                            notifyWarning("Invalid region " + name + ": " + value);
                        }
                    } else {
                        notifyWarning("Invalid region " + name + " value: " + value);
                    }
                    break;
                case "scroll":
                    if (!value.equals("up")) {
                        notifyWarning("Invalid region " + name + " value: " + value);
                    }
                    break;
                default:
                    notifyWarning("Unknown region setting: " + name + ":" + value);
                    break;
            }

        }
    }

    private void parseCueId(VttCue cue, String textLine) throws SubtitleParsingException {
        cue.setId(textLine); // done
    }

    private void parseCueHeader(VttCue cue, String textLine) throws SubtitleParsingException {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            notifyError("Timecode '" + textLine + "' is badly formated");
            return;
        }

        SubtitleTimeCode startTime = parseTimeCode(m.group(1), subtitleOffset);
        cue.setStartTime(startTime);
        SubtitleTimeCode endTime = parseTimeCode(m.group(2), subtitleOffset);
        cue.setEndTime(endTime);

        if (startTime != null && endTime != null) {
            if (startTime.getTime() >= endTime.getTime()) {
                notifyWarning("Invalid cue start time");
            }

            VttCue lastCue = vttObject.getLastCue();
            if (lastCue != null) {
                SubtitleTimeCode lastStartTime = lastCue.getStartTime();
                if (lastStartTime != null) {
                    if (startTime.getTime() < lastStartTime.getTime()) {
                        notifyWarning("Invalid cue start time");
                    }
                }
            }
        }

        // parse cue settings
        String settings = m.group(3);
        parseCueSettings(cue, settings);
    }

    private void parseCueSettings(VttCue cue, String settings) {
        Matcher m = CUE_SETTING_PATTERN.matcher(settings);
        while (m.find()) {
            String name = m.group(1);
            String value = m.group(2);
            try {
                switch (name) {
                    case "vertical":
                        if (!"lr".equals(value) && !"rl".equals(value)) {
                            notifyWarning("Invalid cue setting " + name + ":" + value);
                        }
                        break;
                    case "line":
                        // position,value
                        parseLineAttribute(value);
                        break;
                    case "align":
                        parseTextAlignment(value);
                        break;
                    case "position":
                        // anchor,percentage
                        parsePositionAttribute(value);
                        break;
                    case "size":
                        parsePercentage(value);
                        break;
                    case "region":
                        // region id
                        VttRegion region = vttObject.getRegion(value);
                        if (region == null) {
                            notifyWarning("No REGION with id " + value);
                        }
                        break;
                    default:
                        notifyWarning("Unrecognized cue setting " + name + ":" + value);
                        break;
                }
            } catch (NumberFormatException e) {
                notifyWarning("Invalid cue setting number format: " + settings);
            }
        }
    }

    private void parseLineAttribute(String s) throws NumberFormatException {
        int commaIndex = s.indexOf(',');

        if (commaIndex != -1) {
            parsePositionAnchor(s.substring(commaIndex + 1));
            s = s.substring(0, commaIndex);
        } else {
            // line anchor NONE;
        }

        if (s.endsWith("%")) {
            parsePercentage(s); // line
            //line type FRACTION;
        } else {
            int lineNumber = Integer.parseInt(s);
            if (lineNumber < 0) {
                // WebVTT defines line -1 as last visible row when lineAnchor is ANCHOR_TYPE_START, where-as
                // Cue defines it to be the first row that's not visible.
                lineNumber--;
            }
            //setLine(lineNumber); // line
            //line type NUMBER;
        }
    }

    private void parsePositionAttribute(String s) throws NumberFormatException {
        int commaIndex = s.indexOf(',');

        if (commaIndex != -1) {
            parsePositionAnchor(s.substring(commaIndex + 1));
            s = s.substring(0, commaIndex);
        } else {
            // position NONE

        }
        parsePercentage(s);
    }

    /**
     * Parses a percentage string.
     *
     * @param s The percentage string.
     * @return The parsed value, where 1.0 represents 100%.
     * @throws NumberFormatException If the percentage could not be parsed.
     */
    private float parsePercentage(String s) throws NumberFormatException {
        if (!s.endsWith("%")) {
            throw new NumberFormatException("Percentages must end with %");
        }
        return Float.parseFloat(s.substring(0, s.length() - 1)) / 100;
    }


    private int parsePositionAnchor(String s) {
        switch (s) {
            case "start":
                return VttCue.ANCHOR_START;
            case "center":
            case "middle":
                return VttCue.ANCHOR_MIDDLE;
            case "end":
                return VttCue.ANCHOR_END;
            default:
                notifyWarning("Invalid anchor value: " + s);
                return VttCue.ANCHOR_NONE;
        }
    }

    private SubtitleStyle.TextAlign parseTextAlignment(String s) {
        switch (s) {
            case "start":
            case "left":
                return SubtitleStyle.TextAlign.LEFT;
            case "center":
            case "middle":
                return SubtitleStyle.TextAlign.CENTER;
            case "end":
            case "right":
                return SubtitleStyle.TextAlign.RIGHT;
            default:
                notifyWarning("Invalid alignment value: " + s);
                return null;
        }
    }

    /**
     * Parse the cue text and validate the tags, ...
     * @return The cue tags tree structure
     * @throws SubtitleParsingException
     * @throws IOException
     */
    protected CueTreeNode parseCueTextTree() throws SubtitleParsingException, IOException {
        // FIXME - read by lines/chars instead of whole block to keep better track of current line
        StringBuilder cueText = readBlockLines();

        if (cueText.length() == 0) {
            notifyError("Empty subtitle is not allowed in WebVTT for cue");
        }

        CueTreeNode tree = new CueTreeNode();
        CueTreeNode current = tree;
        TagStatus tagStatus = TagStatus.NONE; // tag parsing status
        int startTag = -1; // where the current tag starts
        int startText = -1; // where the current plain text block starts

        // Process:
        // - voice
        // - class
        // - styles
        int i = 0;
        while (i < cueText.length()) {
            char c = cueText.charAt(i);

            if (c == '<') {
                if (tagStatus == TagStatus.OPEN || tagStatus == TagStatus.CLOSE) {
                    notifyError("Invalid character inside a cue tag: '<'");
                }

                int endText = i;
                if (i + 1 < cueText.length()) {
                    c = cueText.charAt(i + 1);
                    if (c == '/') {
                        i++;
                        tagStatus = TagStatus.CLOSE;
                    } else {
                        tagStatus = TagStatus.OPEN;
                    }
                    startTag = i + 1;
                }

                // Add accumulated plain text if any
                if (startText >= 0) {
                    String text = cueText.substring(startText, endText);
                    CueTreeNode plainChild = new CueTreeNode(new CuePlainData(text));
                    current.add(plainChild);
                    startText = -1;
                }

            } else if (c == '>') {
                int endTag = i;
                if (tagStatus == TagStatus.NONE) {
                    notifyError("Invalid character outside a cue tag: '>'");
                }

                // Close tag
                if (tagStatus == TagStatus.OPEN) {
                    // create cue element
                    String tag = cueText.substring(startTag, endTag);
                    if (tag.isEmpty()) {
                        notifyWarning("The cue tag is empty");
                    }
                    validateTag(tag);

                    CueTreeNode elemChild = new CueTreeNode(new CueElemData(tag));
                    current.add(elemChild);
                    // move down
                    current = elemChild;
                } else if (tagStatus == TagStatus.CLOSE) {
                    // close cue element
                    // match with start tag
                    String tag = cueText.substring(startTag, endTag);
                    if (!tag.equals(current.getData().getTag())) {
                        String msg = "Unmatched end tag: " + tag;
                        notifyWarning(msg);
                    }
                    // move up
                    current = current.getParent();
                }
                startText = i + 1;
                tagStatus = TagStatus.NONE;

            } else if (c == '&') {
                if (startText < 0) {
                    startText = i;
                }
                int ss = i + 1;
                while (i < cueText.length() && c != ';' && c != ' ') {
                    c = cueText.charAt(i++);
                }
                int se = (i == cueText.length()) ? i : i - 1;
                String charEntity = cueText.substring(ss, se);
                validateEntity(charEntity);
            } else {
                if (startText < 0) {
                    startText = i;
                }
            }
            i++;
        }

        if (tagStatus != TagStatus.NONE) {
            notifyWarning("Disclosed tag: <" + cueText.substring(startTag));
        }

        // Add last accumulated plain text if any
        if (startText >= 0 && startText < cueText.length() - 1) {
            String text = cueText.substring(startText, cueText.length());
            CueTreeNode plainChild = new CueTreeNode(new CuePlainData(text));
            current.add(plainChild);
        }

        while (current != null && current != tree) {
            notifyWarning("Missing close tag: </" + current.getData().getTag() + ">");
            current = current.getParent();
        }

        return tree;
    }

    protected SubtitleTimeCode parseTimeCode(String timeCodeString, int subtitleOffset) throws SubtitleParsingException {
        long value = 0;
        String[] parts = timeCodeString.split("\\.", 2);
        if (parts.length > 2) {
            String msg = String.format("Invalid time value: %s", timeCodeString);
            notifyError(msg);
            return null;
        }
        String[] subparts = parts[0].split(":");
        if (subparts.length > 3) {
            String msg = String.format("Invalid time value: %s", timeCodeString);
            notifyError(msg);
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

    private void validateEntity(String entity) {
        switch (entity) {
            case "lt": // <
            case "gt": // >
            case "nbsp": // ' '
            case "amp": // &
                break;
            default:
                notifyWarning("Unsupported entity: '&" + entity + ";'");
                break;
        }
    }

    private void validateTag(String tagName) {
        switch (tagName) {
            case "b":
            case "c":
            case "i":
            case "lang":
            case "u":
            case "v":
                // ok
                break;
            default:
                notifyWarning("Unknown cue tag: <" + tagName + ">");
                break;
        }
    }
}