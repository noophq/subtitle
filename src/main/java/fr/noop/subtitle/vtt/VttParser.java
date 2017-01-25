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
import fr.noop.subtitle.base.CueData;
import fr.noop.subtitle.base.CueElemData;
import fr.noop.subtitle.base.CuePlainData;
import fr.noop.subtitle.base.CueTimeStampData;
import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleStyle;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern CUE_TIME_TAG_PATTERN = Pattern.compile("^(\\d+)(:\\d+)*(\\.\\d+)?$");

    private static final String[] EMPTY_CLASSES = new String[0];

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
    private VttCue currentVttCue; // current VttCue

    private int maxDuration = Integer.MAX_VALUE;
    private LineNumberReader br;

    public VttParser(Charset charset) {
        this.charset = charset;
    }

    public int getLineNumber() {
        return (br == null) ? 0 : br.getLineNumber();
    }

    public int getColumn() {
        return 0; // TODO
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
        inCues = false;
        vttObject = new VttObject(); // Create vtt object
        currentVttCue = null;

        // Read each lines
        try (LineNumberReader lnrd = new LineNumberReader(new InputStreamReader(is, this.charset))) {

            br = lnrd;
            parseWebVTT();

            String textLine;
            while ((textLine = br.readLine()) != null) {
                // All Vtt files start with WEBVTT
                textLine = textLine.replace('\u0000', '\uFFFD');
                CursorStatus event = getNextEvent(textLine, inCues);

                currentVttCue = null;
                switch (event) {
                    case CUE_ID:
                        currentVttCue = new VttCue();
                        parseCueId(currentVttCue, textLine); // ???
                        if ((textLine = br.readLine()) == null) {
                            break;
                        }
                        textLine = textLine.replace('\u0000', '\uFFFD');
                        // $FALLTHROUGH
                    case CUE_TIMECODE:
                        // textLine defines the start and end time codes
                        // 00:01:21.456 --> 00:01:23.417
                        inCues = true;
                        if (currentVttCue == null) {
                            currentVttCue = new VttCue();
                        }
                        parseCueHeader(textLine);

                        // End of cue
                        // Process multilines text in one time
                        // A class or a style can be applied for more than one line
                        CueTreeNode node = parseCueTextTree();
                        currentVttCue.setTree(node);
                        vttObject.addCue(currentVttCue);
                        break;
                    case NOTE:
                        VttNote vttNote = new VttNote();
                        parseNote(vttNote);
                        vttObject.addNote(vttNote);
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
        String textLine = br.readLine();
        if (textLine == null || !WEBVTT.matcher(textLine).matches()) {
            notifyError("Invalid WEBVTT header " + textLine);
            return;
        }

        // read up to two new lines
        readBlockLines();
    }

    private void parseNote(VttNote note) throws SubtitleParsingException, IOException {
        StringBuilder noteText = readBlockLines();
        if (noteText.indexOf(ARROW) >= 0) {
            notifyError("'" + ARROW + "' found inside comment block");
        }
        else {
            note.setNote(noteText.toString());
        }
    }

    private void parseStyle(VttStyle style) throws SubtitleParsingException, IOException {
        StringBuilder styleText = readBlockLines();
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
                        region.setWidth(parsePercentage(value));
                    } catch (NumberFormatException e) {
                        notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "lines":
                    try {
                        region.setLines(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "viewportanchor":
                    float[] vret = parseFloatCouple(value);
                    if (vret != null) {
                        region.setViewPortAnchor(vret);
                    }
                    break;
                case "regionanchor":
                    float[] aret = parseFloatCouple(value);
                    if (aret != null) {
                        region.setRegionAnchor(aret);
                    }
                    break;
                case "scroll":
                    if (!value.equals("up")) {
                        notifyWarning("Invalid region " + name + " value: " + value);
                    }
                    else {
                        region.setScrollUp(true);
                    }
                    break;
                default:
                    notifyWarning("Unknown region setting: " + name + ":" + value);
                    break;
            }
        }
    }

    private float[] parseFloatCouple(String value) {
        String parts[] = value.split(",");
        if (parts.length == 2) {
            float[] ret = new float[2];
            try {
                ret[0] = parsePercentage(parts[0]);
                ret[1] = parsePercentage(parts[1]);
                return ret;
            } catch (NumberFormatException e) {
                notifyWarning("Invalid region setting: " + value);
            }
        } else {
            notifyWarning("Invalid region setting: " + value);
        }
        return null;
    }

    private void parseCueId(VttCue cue, String textLine) throws SubtitleParsingException {
        cue.setId(textLine); // done
    }

    private void parseCueHeader(String textLine) throws SubtitleParsingException {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            notifyError("Timecode '" + textLine + "' is badly formated");
            return;
        }

        SubtitleTimeCode startTime = parseTimeCode(m.group(1), 0);
        currentVttCue.setStartTime(startTime);
        SubtitleTimeCode endTime = parseTimeCode(m.group(2), 0);
        currentVttCue.setEndTime(endTime);

        if (startTime != null && endTime != null) {
            if (startTime.getTime() >= endTime.getTime()) {
                notifyWarning("Invalid cue start time");
            }

            VttCue lastCue = (VttCue) vttObject.getLastCue();
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
        parseCueSettings(settings);
    }

    private void parseCueSettings(String settings) {
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
            parseLineAnchor(s.substring(commaIndex + 1));
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
            throw new NumberFormatException("Percentage must end with %: " + s);
        }
        float f = Float.parseFloat(s.substring(0, s.length() - 1)) / 100f;
        if (f < 0f || f > 1f) {
            throw new NumberFormatException("Percentage must be within 0..100: " + s);
        }
        return f;
    }

    private int parseLineAnchor(String s) {
        switch (s) {
            case "start":
                return VttCue.ANCHOR_START;
            case "center":
            //case "middle":
                return VttCue.ANCHOR_MIDDLE;
            case "end":
                return VttCue.ANCHOR_END;
            default:
                notifyWarning("Invalid line anchor value: " + s);
                return VttCue.ANCHOR_NONE;
        }
    }

    private int parsePositionAnchor(String s) {
        switch (s) {
            case "line-left":
                return VttCue.ANCHOR_START;
            case "center":
                return VttCue.ANCHOR_MIDDLE;
            case "line-right":
                return VttCue.ANCHOR_END;
            default:
                notifyWarning("Invalid position anchor value: " + s);
                return VttCue.ANCHOR_NONE;
        }
    }

    private SubtitleStyle.TextAlign parseTextAlignment(String s) {
        switch (s) {
            case "start":
            case "left":
                return SubtitleStyle.TextAlign.LEFT;
            case "center":
            //case "middle":
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
    protected CueTreeNode parseCueTextTree()
            throws SubtitleParsingException, IOException {

        // FIXME - read by lines/chars instead of whole block to keep better track of current line
        StringBuilder cueText = readBlockLines();

        if (cueText.length() == 0) {
            notifyError("Empty cue is not allowed");
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
                    else {
                        CueData cueTag = startTag(current, tag);
                        CueTreeNode elemChild = new CueTreeNode(cueTag);
                        current.add(elemChild);
                        // move down
                        current = elemChild;
                    }
                } else if (tagStatus == TagStatus.CLOSE) {
                    // close cue element
                    // match with start tag
                    String tag = cueText.substring(startTag, endTag);
                    // try to find a matching tag in parent
                    CueTreeNode matchNode = current.findParentByTag(tag);
                    if (matchNode != current) {
                        String msg = "Unmatched end tag: " + tag;
                        notifyWarning(msg);
                    }
                    CueTreeNode closing = current;
                    if (matchNode != null) {
                        // close nodes up to the one that matches the tag
                        CueTreeNode stopNode = matchNode.getParent();
                        while (closing != stopNode) {
                            // close the node and move up
                            endTag(closing);
                            closing = closing.getParent();
                        }
                    }
                    current = closing;
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
            // FIXME - normalize to "&lt;text"
            // CueTreeNode plainChild = new CueTreeNode(new CuePlainData("&lt;" + cueText.substring(startTag)));
            // current.add(plainChild);
        }

        // Add last accumulated plain text if any
        if (startText >= 0 && startText < cueText.length() - 1) {
            String text = cueText.substring(startText, cueText.length());
            CueTreeNode plainChild = new CueTreeNode(new CuePlainData(text));
            current.add(plainChild);
        }

        while (current != null && current != tree) {
            notifyWarning("Missing close tag: </" + current.getTag() + ">");
            current = current.getParent();
        }

        return tree;
    }

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

    private CueData startTag(CueTreeNode current, String wholeName) {
        String annotation;

        int annotationStartIndex = wholeName.indexOf(' ');
        if (annotationStartIndex == -1) {
            annotation = "";
        } else {
            annotation = wholeName.substring(annotationStartIndex).trim();
            wholeName = wholeName.substring(0, annotationStartIndex);
        }

        // timestamp tag
        if (CUE_TIME_TAG_PATTERN.matcher(wholeName).matches()) {
            if (!annotation.isEmpty()) {
                notifyWarning("Annotation in timestamp tag");
            }
            return createTimestampTag(wholeName);
        }

        String[] nameAndClasses = wholeName.split("\\.");
        String tagName = nameAndClasses[0];

        String[] classes;
        if (nameAndClasses.length > 1) {
            classes = Arrays.copyOfRange(nameAndClasses, 1, nameAndClasses.length);
        } else {
            classes = EMPTY_CLASSES;
        }

        switch (tagName) {
            case "c": // just styling
                if (classes.length == 0) {
                    notifyWarning("No classes specified in <" + tagName + "> tag");
                }
                //$FALLTHROUGH
            case "b": // just styling
            case "i": // just styling
            case "u": // just styling
                if (!annotation.isEmpty()) {
                    notifyWarning("Annotation specified in <" + tagName + "> tag");
                }
                break;
            case "lang": // language
                if (annotation.isEmpty()) {
                    notifyWarning("No language specified in <" + tagName + ">");
                    annotation = "";
                }
                break;
            case "v":
                // does not need end tag if it is the only tag in the text
                if (annotation.isEmpty()) {
                    notifyWarning("No voice specified in <v> tag");
                }
                break;
            case "ruby":
                // no annotation, only rt inside
                if (!annotation.isEmpty()) {
                    notifyWarning("No annotation allowed for <ruby> tag");
                    annotation = "";
                }
                if (current.findParentByTag("ruby") != null) {
                    notifyWarning("Nested <ruby> tag not allowed");
                }
                break;
            case "rt": // only inside ruby, does not need end tag, no annotation
                if (!"ruby".equals(current.getTag())) {
                    notifyWarning("<rt> cannot be outside <ruby>");
                }
                if (classes.length > 0 || !annotation.isEmpty()) {
                    notifyWarning("No annotation allowed for <rt> tag");
                    annotation = "";
                    classes = EMPTY_CLASSES;
                }
                break;
            default:
                notifyWarning("Unknown cue tag: <" + tagName + ">");
                break;
        }

        if (!"rt".equals(tagName) && "ruby".equals(current.getTag())) {
            notifyWarning("<ruby> tag can contain only <rt>");
        }

        CueElemData cueTag = new CueElemData(tagName, classes, annotation);
        return cueTag;
    }

    private CueData createTimestampTag(String wholeName) {
        SubtitleTimeCode middleTime = parseTimeCode(wholeName, 0);

        CueTimeStampData middleStamp = new CueTimeStampData(middleTime);
        if (middleTime != null) {
            // validate timing sequence
            SubtitleTimeCode startTime = currentVttCue.getStartTime();
            if (startTime != null && middleTime.compareTo(startTime) < 0) {
                notifyWarning("Timestamp before cue start time");
            }
            SubtitleTimeCode endTime = currentVttCue.getEndTime();
            if (endTime != null && middleTime.compareTo(endTime) > 0) {
                notifyWarning("Timestamp after cue end time");
            }
        }
        return middleStamp;
    }

    private void endTag(CueTreeNode current) {
        if ("rt".equals(current.getTag())) {
            if (current.hasSubTags()) {
                notifyWarning("<rt> tag can contain only plain text");
            }
        }
    }
}