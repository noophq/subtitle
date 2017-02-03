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

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.base.CueData;
import fr.noop.subtitle.base.CueElemData;
import fr.noop.subtitle.base.CuePlainData;
import fr.noop.subtitle.base.CueTimeStampData;
import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.model.ValidationReporter;
import fr.noop.subtitle.util.SubtitleStyle;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttCue extends BaseSubtitleCue {
    private enum TagStatus {
        NONE,
        OPEN,
        CLOSE
    }

    private static final String TAG_BOLD = "b";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_CLASS = "c";
    private static final String TAG_VOICE = "v";
    private static final String TAG_LANG = "lang";
    private static final String TAG_RUBY = "ruby";
    private static final String TAG_RT = "rt";

    private static final int ANCHOR_NONE = 0;
    private static final int ANCHOR_START = 1;
    private static final int ANCHOR_MIDDLE = 2;
    private static final int ANCHOR_END = 3;

    private static final Pattern CUE_TIME_PATTERN = Pattern.compile("^\\s*(\\S+)\\s+" + VttParser.ARROW + "\\s+(\\S+)(.*)?$");
    private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");

    private static final Pattern CUE_TIME_TAG_PATTERN = Pattern.compile("^(\\d+)(:\\d+)*(\\.\\d+)?$");

    private static final String[] EMPTY_CLASSES = new String[0];

    private CueTreeNode tree;
    private ValidationReporter reporter;
    private VttObject vtt;

    public VttCue(ValidationReporter reporter, VttObject vtt) {
        this.reporter = reporter;
        this.vtt = vtt;
    }

    public CueTreeNode getTree() {
        return this.tree;
    }

    void parseCueId(String textLine) throws SubtitleParsingException {
        setId(textLine); // done
    }

    void parseCueHeader(String textLine) throws SubtitleParsingException {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            reporter.notifyError("Timecode '" + textLine + "' is badly formated");
            return;
        }

        SubtitleTimeCode startTime = parseTimeCode(m.group(1), 0);
        setStartTime(startTime);
        SubtitleTimeCode endTime = parseTimeCode(m.group(2), 0);
        setEndTime(endTime);

        if (startTime != null && endTime != null) {
            if (startTime.getTime() >= endTime.getTime()) {
                reporter.notifyWarning("Invalid cue start time");
            }

            if (vtt != null) {
                VttCue lastCue = (VttCue) vtt.getLastCue();
                if (lastCue != null) {
                    SubtitleTimeCode lastStartTime = lastCue.getStartTime();
                    if (lastStartTime != null) {
                        if (startTime.getTime() < lastStartTime.getTime()) {
                            reporter.notifyWarning("Invalid cue start time");
                        }
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
                            reporter.notifyWarning("Invalid cue setting " + name + ":" + value);
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
                        VttParser.parsePercentage(value);
                        break;
                    case "region":
                        // region id
                        if (vtt != null) {
                            VttRegion region = vtt.getRegion(value);
                            if (region == null) {
                                reporter.notifyWarning("No REGION with id " + value);
                            }
                        }
                        break;
                    default:
                        reporter.notifyWarning("Unrecognized cue setting " + name + ":" + value);
                        break;
                }
            } catch (NumberFormatException e) {
                reporter.notifyWarning("Invalid cue setting number format: " + settings);
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
            VttParser.parsePercentage(s); // line
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
        VttParser.parsePercentage(s);
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
                reporter.notifyWarning("Invalid line anchor value: " + s);
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
                reporter.notifyWarning("Invalid position anchor value: " + s);
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
                reporter.notifyWarning("Invalid alignment value: " + s);
                return null;
        }
    }

    /**
     * Parse the cue text and validate the tags, ...
     * @return The cue tags tree structure
     * @throws SubtitleParsingException
     * @throws IOException
     */
    void parseCueTextTree(StringBuilder cueText) throws SubtitleParsingException, IOException {
        // FIXME - read by lines/chars instead of whole block to keep better track of current line

        if (cueText.length() == 0) {
            reporter.notifyError("Empty cue is not allowed");
        }

        tree = new CueTreeNode();
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
                    reporter.notifyWarning("Probably disclosed tag: <" + cueText.substring(startTag));
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
                    reporter.notifyError("Invalid character outside a cue tag: '>'");
                }

                // Close tag
                if (tagStatus == TagStatus.OPEN) {
                    // create cue element
                    String tag = cueText.substring(startTag, endTag);
                    if (tag.isEmpty()) {
                        reporter.notifyWarning("The cue tag is empty");
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
                        reporter.notifyWarning(msg);
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
                parseEntity(charEntity);
            } else {
                if (startText < 0) {
                    startText = i;
                }
            }
            i++;
        }

        if (tagStatus != TagStatus.NONE) {
            reporter.notifyWarning("Disclosed tag: <" + cueText.substring(startTag));
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
            reporter.notifyWarning("Missing close tag: </" + current.getTag() + ">");
            current = current.getParent();
        }
    }

    protected SubtitleTimeCode parseTimeCode(String timeCodeString, int subtitleOffset) {
        long value = 0;
        String[] parts = timeCodeString.split("\\.", 2);
        if (parts.length > 2) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
            return null;
        }
        String[] subparts = parts[0].split(":");
        if (subparts.length > 3) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
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
            reporter.notifyError("Invalid time format: " + timeCodeString);
        } catch (IllegalArgumentException e) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
        }
        return null;
    }

    private void parseEntity(String entity) {
        switch (entity) {
            case "lt": // <
            case "gt": // >
            case "nbsp": // ' '
            case "amp": // &
                break;
            default:
                reporter.notifyWarning("Unsupported entity: '&" + entity + ";'");
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
                reporter.notifyWarning("Annotation in timestamp tag");
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
                    reporter.notifyWarning("No classes specified in <" + tagName + "> tag");
                }
                //$FALLTHROUGH
            case "b": // just styling
            case "i": // just styling
            case "u": // just styling
                if (!annotation.isEmpty()) {
                    reporter.notifyWarning("Annotation specified in <" + tagName + "> tag");
                }
                break;
            case "lang": // language
                if (annotation.isEmpty()) {
                    reporter.notifyWarning("No language specified in <" + tagName + ">");
                    annotation = "";
                }
                break;
            case "v":
                // does not need end tag if it is the only tag in the text
                if (annotation.isEmpty()) {
                    reporter.notifyWarning("No voice specified in <v> tag");
                }
                break;
            case "ruby":
                // no annotation, only rt inside
                if (!annotation.isEmpty()) {
                    reporter.notifyWarning("No annotation allowed for <ruby> tag");
                    annotation = "";
                }
                if (current.findParentByTag("ruby") != null) {
                    reporter.notifyWarning("Nested <ruby> tag not allowed");
                }
                break;
            case "rt": // only inside ruby, does not need end tag, no annotation
                if (!"ruby".equals(current.getTag())) {
                    reporter.notifyWarning("<rt> cannot be outside <ruby>");
                }
                if (classes.length > 0 || !annotation.isEmpty()) {
                    reporter.notifyWarning("No annotation allowed for <rt> tag");
                    annotation = "";
                    classes = EMPTY_CLASSES;
                }
                break;
            default:
                reporter.notifyWarning("Unknown cue tag: <" + tagName + ">");
                break;
        }

        if (!"rt".equals(tagName) && "ruby".equals(current.getTag())) {
            reporter.notifyWarning("<ruby> tag can contain only <rt>");
        }

        CueElemData cueTag = new CueElemData(tagName, classes, annotation);
        return cueTag;
    }

    private CueData createTimestampTag(String wholeName) {
        SubtitleTimeCode middleTime = parseTimeCode(wholeName, 0);

        CueTimeStampData middleStamp = new CueTimeStampData(middleTime);
        if (middleTime != null) {
            // validate timing sequence
            SubtitleTimeCode startTime = getStartTime();
            if (startTime != null && middleTime.compareTo(startTime) < 0) {
                reporter.notifyWarning("Timestamp before cue start time");
            }
            SubtitleTimeCode endTime = getEndTime();
            if (endTime != null && middleTime.compareTo(endTime) > 0) {
                reporter.notifyWarning("Timestamp after cue end time");
            }
        }
        return middleStamp;
    }

    private void endTag(CueTreeNode current) {
        if ("rt".equals(current.getTag())) {
            if (current.hasSubTags()) {
                reporter.notifyWarning("<rt> tag can contain only plain text");
            }
        }
    }


}
