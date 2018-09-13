/*
 * Title: VttCue
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

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.base.CueData;
import com.blackboard.collaborate.validator.subtitle.base.CueElemData;
import com.blackboard.collaborate.validator.subtitle.base.CuePlainData;
import com.blackboard.collaborate.validator.subtitle.base.CueTreeNode;
import com.blackboard.collaborate.validator.subtitle.base.TagStatus;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.EntityParser;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleStyle;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;
import com.blackboard.collaborate.validator.subtitle.util.TimeCodeParser;
import lombok.Getter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttCue extends BaseSubtitleCue {
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

    private static final String[] EMPTY_CLASSES = new String[0];

    @Getter
    private CueTreeNode tree;

    private final ValidationReporter reporter;
    private final VttObject vttObject;
    private Map<String, String> settingsMap;

    public VttCue(ValidationReporter reporter, VttObject vttObject) {
        this.reporter = reporter;
        this.vttObject = vttObject;
    }

    void parseCueId(String textLine) {
        setId(textLine); // done
    }

    boolean parseCueHeader(String textLine) {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            reporter.notifyError("Timecode '" + textLine + "' is badly formated");
            return false;
        }

        SubtitleTimeCode startTime = TimeCodeParser.parseVtt(reporter, m.group(1), 0);
        setStartTime(startTime);
        SubtitleTimeCode endTime = TimeCodeParser.parseVtt(reporter, m.group(2), 0);
        setEndTime(endTime);

        if (startTime != null && endTime != null) {
            if (!startTime.isBefore(endTime)) {
                reporter.notifyWarning("Invalid cue start time");
            }

            if (vttObject != null) {
                VttCue lastCue = (VttCue) vttObject.getLastCue();
                if (lastCue != null) {
                    SubtitleTimeCode lastStartTime = lastCue.getStartTime();
                    if (lastStartTime != null) {
                        if (startTime.isBefore(lastStartTime)) {
                            reporter.notifyWarning("Invalid cue start time");
                        }
                    }
                }
            }
        }

        // parse cue settings
        String settings = m.group(3);
        parseCueSettings(settings);
        return true;
    }

    private void addCueSetting(String name, String value) {
        if (settingsMap.put(name, value) != null) {
            reporter.notifyWarning("Duplicated cue setting: " + name);
        }
    }

    private void parseCueSettings(String settings) {
        Matcher m = CUE_SETTING_PATTERN.matcher(settings);
        settingsMap = new HashMap<>();
        while (m.find()) {
            String name = m.group(1);
            String value = m.group(2);
            try {
                switch (name) {
                    case "vertical":
                        if (!"lr".equals(value) && !"rl".equals(value)) {
                            reporter.notifyWarning("Invalid cue setting " + name + ":" + value);
                        }
                        else {
                            addCueSetting(name, value);
                        }
                        break;
                    case "line":
                        // position,value
                        parseLineAttribute(value);
                        addCueSetting(name, value);
                        break;
                    case "align":
                        if (parseTextAlignment(value) != null) {
                            addCueSetting(name, value);
                        }
                        break;
                    case "position":
                        // anchor,percentage
                        parsePositionAttribute(value);
                        addCueSetting(name, value);
                        break;
                    case "size":
                        VttParser.parsePercentage(value);
                        addCueSetting(name, value);
                        break;
                    case "region":
                        // region id
                        if (vttObject != null) {
                            VttRegion region = vttObject.getRegion(value);
                            if (region == null) {
                                reporter.notifyWarning("No REGION with id " + value);
                            }
                        }
                        addCueSetting(name, value);
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
     * Visible for unit tests.
     * @throws IOException when an IO exception occur
     */
    void parseCueText(SubtitleReader reader) throws IOException {
        tree = new CueTreeNode();
        CueTreeNode current = tree;
        TagStatus tagStatus = TagStatus.NONE; // tag parsing status
        StringBuilder tagBuilder = new StringBuilder(); // tag name
        StringBuilder textBuilder = new StringBuilder(); // plain text
        int len = 0;
        boolean wasNL = false;

        // Process:
        // - voice
        // - class
        // - styles
        do {
            int c = reader.read();
            if (c == -1) {
                if (len == 0) {
                    reporter.notifyError("Empty cue is not allowed");
                }
                break; // end of file
            } else if (c == '\n') {
                if (wasNL) {
                    break; // double new line
                }
                wasNL = true;
            } else if (c != ' ' && c != '\t') {
                wasNL = false;
            }
            len++;

            if (c == '-') {
                reader.mark(2);
                if (reader.read() == '-' && reader.read() == '>') {
                    reporter.notifyError("Detected '" + VttParser.ARROW + "' inside cue text");
                } else {
                    reader.reset();
                }
            }

            if (c == '<') {
                if (tagStatus == TagStatus.OPEN || tagStatus == TagStatus.CLOSE) {
                    reporter.notifyWarning("Probably a disclosed tag: <");
                }

                c = reader.lookNext();
                if (c != -1) {
                    if (c == '/') {
                        len += reader.skip(1);
                        tagStatus = TagStatus.CLOSE;
                    } else {
                        tagStatus = TagStatus.OPEN;
                    }
                    tagBuilder.setLength(0);
                }

                // Add accumulated plain text if any
                if (textBuilder.length() > 0) {
                    CueTreeNode plainChild = new CueTreeNode(new CuePlainData(textBuilder.toString()));
                    current.add(plainChild);
                    textBuilder.setLength(0);
                }
            } else if (c == '>') {
                if (tagStatus == TagStatus.NONE) {
                    reporter.notifyError("Invalid character outside a cue tag: '>'");
                }

                // Close tag
                if (tagStatus == TagStatus.OPEN) {
                    // create cue element

                    if (tagBuilder.length() == 0) {
                        reporter.notifyWarning("The cue tag is empty");
                    } else {
                        CueData cueTag = startTag(current, tagBuilder.toString());
                        CueTreeNode elemChild = new CueTreeNode(cueTag);
                        current.add(elemChild);
                        if (!(cueTag instanceof CueTimeStampData)) { // TODO: think of better design to avoid 'instanceof'
                            // move down, but not for timestamp
                            current = elemChild;
                        }
                    }
                } else if (tagStatus == TagStatus.CLOSE) {
                    // close cue element
                    // match with start tag
                    String tag = tagBuilder.toString();
                    // try to find a matching tag in parent
                    CueTreeNode matchNode = current.findParentByTag(tag);
                    if (matchNode != current) {
                        reporter.notifyWarning("Unmatched end tag: " + tag);
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
                textBuilder.setLength(0);
                tagStatus = TagStatus.NONE;

            } else if (c == '&') {
                StringBuilder entity = new StringBuilder();
                c = reader.read();
                while (c != -1 && c != '<' && c != '&' && !Character.isWhitespace(c) && c != ';') {
                    entity.append((char) c);
                    c = reader.read();
                }
                if (c != ';') {
                    reporter.notifyWarning("Missing ';' in entity " + entity);
                }
                EntityParser.parse(reporter, entity.toString());
                if (tagStatus == TagStatus.NONE) {
                    textBuilder.append("&" + entity + ";"); // TODO: add resolved?
                } else {
                    tagBuilder.append("&" + entity + ";"); // TODO: add resolved?
                }
            }
            else {
                switch (tagStatus) {
                    case CLOSE:
                    case OPEN:
                        tagBuilder.append((char) c);
                        break;
                    case NONE:
                        textBuilder.append((char) c);
                        break;
                }
            }
        } while (true);

        if (tagStatus != TagStatus.NONE) {
            reporter.notifyWarning("Disclosed tag: <" + tagBuilder.toString());
            // TODO: replace with "&lt;text" and continue
            // CueTreeNode plainChild = new CueTreeNode(new CuePlainData("&lt;" + cueText.substring(startTag)));
            // current.add(plainChild);
        }

        // Add last accumulated plain text if any
        if (textBuilder.length() > 0) {
            CueTreeNode plainChild = new CueTreeNode(new CuePlainData(textBuilder.toString()));
            current.add(plainChild);
        }

        while (current != null && current != tree) {
            if (!current.isLeaf()) {
                // <v> does not need end tag
                if (!TAG_VOICE.equals(current.getTag())) {
                    reporter.notifyWarning("Missing close tag: </" + current.getTag() + ">");
                }
            }
            current = current.getParent();
        }
    }

    private void checkNesting(CueTreeNode current, String tag) {
        if (current.findParentByTag(tag) != null) {
            reporter.notifyWarning("Nested <" + tag + "> tag not allowed");
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
        if (TimeCodeParser.matchesVtt(wholeName)) {
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
            case TAG_CLASS: // just styling
                if (classes.length == 0) {
                    reporter.notifyWarning("No classes specified in <" + tagName + "> tag");
                }
                //$FALLTHROUGH
            case TAG_BOLD: // just styling
            case TAG_ITALIC: // just styling
            case TAG_UNDERLINE: // just styling
                if (!annotation.isEmpty()) {
                    reporter.notifyWarning("Annotation specified in <" + tagName + "> tag");
                }
                break;
            case TAG_LANG: // language
                if (annotation.isEmpty()) {
                    reporter.notifyWarning("No language specified in <" + tagName + ">");
                    annotation = "";
                }
                break;
            case TAG_VOICE:
                // does not need end tag if it is the only tag in the text
                if (annotation.isEmpty()) {
                    reporter.notifyWarning("No voice specified in " + tagName + " tag");
                }
                break;
            case TAG_RUBY:
                // no annotation, only rt inside
                if (!annotation.isEmpty()) {
                    reporter.notifyWarning("No annotation allowed for <" + tagName + "> tag");
                    annotation = "";
                }
                break;
            case TAG_RT: // only inside ruby, does not need end tag, no annotation
                if (!"ruby".equals(current.getTag())) {
                    reporter.notifyWarning("Tag <" + tagName + "> cannot be outside tag >" + TAG_RUBY + ">");
                }
                if (classes.length > 0 || !annotation.isEmpty()) {
                    reporter.notifyWarning("No annotation allowed for <" + tagName + "> tag");
                    annotation = "";
                    classes = EMPTY_CLASSES;
                }
                break;
            default:
                reporter.notifyWarning("Unknown cue tag: <" + tagName + ">");
                break;
        }

        if (!TAG_CLASS.equals(tagName)) {
            checkNesting(current, tagName);
        }

        if (!TAG_RT.equals(tagName) && TAG_RUBY.equals(current.getTag())) {
            reporter.notifyWarning("Tag <" + TAG_RUBY + "> can contain only <" + TAG_RT + "> tags");
        }

        return new CueElemData(tagName, classes, annotation);
    }

    private CueData createTimestampTag(String wholeName) {
        SubtitleTimeCode middleTime = TimeCodeParser.parseVtt(reporter, wholeName, 0);

        CueTimeStampData middleStamp = new CueTimeStampData(middleTime);
        if (middleTime != null) {
            // validate timing sequence
            SubtitleTimeCode startTime = getStartTime();
            if (startTime != null && middleTime.isBefore(startTime)) {
                reporter.notifyWarning("Timestamp before cue start time");
            }
            SubtitleTimeCode endTime = getEndTime();
            if (endTime != null && middleTime.isAfter(endTime)) {
                reporter.notifyWarning("Timestamp after cue end time");
            }
        }
        return middleStamp;
    }

    private void endTag(CueTreeNode current) {
        if (TAG_RT.equals(current.getTag())) {
            if (current.hasSubTags()) {
                reporter.notifyWarning("<" + TAG_RT + "> tag can contain only plain text");
            }
        }
    }

    @Override
    public String getText() {
        return tree.toStyledString();
    }

    @Override
    public Iterable<Map.Entry<String, String>> getSettings() {
        return settingsMap.entrySet();
    }
}
