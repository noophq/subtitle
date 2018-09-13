/*
 * Title: SrtCue
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

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.base.CueData;
import com.blackboard.collaborate.validator.subtitle.base.CueElemData;
import com.blackboard.collaborate.validator.subtitle.base.CuePlainData;
import com.blackboard.collaborate.validator.subtitle.base.CueTreeNode;
import com.blackboard.collaborate.validator.subtitle.base.TagStatus;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.EntityParser;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;
import com.blackboard.collaborate.validator.subtitle.util.TimeCodeParser;
import lombok.Getter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SrtCue extends BaseSubtitleCue {
    private static final String TAG_BOLD = "b";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_UNDERLINE = "u";
    private static final String[] EMPTY_CLASSES = new String[0];

    static final String ARROW = "-->";
    private static final Pattern CUE_TIME_PATTERN = Pattern.compile("^\\s*(\\S+)\\s+" + ARROW + "\\s+(\\S+)\\s*$");

    private final ValidationReporter reporter;
    private final SrtObject srtObject;

    @Getter
    private CueTreeNode tree;

    public SrtCue(ValidationReporter reporter, SrtObject srtObject) {
        this.reporter = reporter;
        this.srtObject = srtObject;
    }

    int parseCueId(String textLine, int lastCueNumber) {
        // First textLine is the cue number
        int cueNumber;
        try {
            cueNumber = Integer.parseInt(textLine);
            if (cueNumber != lastCueNumber + 1) {
                reporter.notifyWarning("Out of order cue number: " + textLine);
            }
        } catch (NumberFormatException e) {
            reporter.notifyError("Unable to parse cue number: " + textLine);
            cueNumber = lastCueNumber;
        }

        setId(textLine);
        return cueNumber;
    }

    boolean parseCueHeader(String textLine, int subtitleOffset) {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            reporter.notifyError("Timecode '" + textLine + "' is badly formated");
            return false;
        }

        SubtitleTimeCode startTime = TimeCodeParser.parseSrt(reporter, m.group(1), subtitleOffset);
        setStartTime(startTime);
        SubtitleTimeCode endTime = TimeCodeParser.parseSrt(reporter, m.group(2), subtitleOffset);
        setEndTime(endTime);

        if (startTime != null && endTime != null) {
            if (!startTime.isBefore(endTime)) {
                reporter.notifyWarning("Invalid cue start time");
            }

            if (srtObject != null) {
                SrtCue lastCue = (SrtCue) srtObject.getLastCue();
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
        return true;
    }

    void parseCueText(SubtitleReader reader) throws IOException {
        tree = new CueTreeNode();
        CueTreeNode current = tree;
        TagStatus tagStatus = TagStatus.NONE; // tag parsing status
        StringBuilder tagBuilder = new StringBuilder(); // tag name
        StringBuilder textBuilder = new StringBuilder(); // plain text
        int len = 0;
        boolean wasNL = false;

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
                    reporter.notifyError("Detected '" + SrtCue.ARROW + "' inside cue text");
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
                        // move down
                        current = elemChild;
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
                reporter.notifyWarning("Missing close tag: </" + current.getTag() + ">");
            }
            current = current.getParent();
        }
    }

    private CueData startTag(CueTreeNode current, String tagName) {
        String annotation;

        switch (tagName) {
            case TAG_BOLD: // just styling
            case TAG_ITALIC: // just styling
            case TAG_UNDERLINE: // just styling
                break;
            default:
                reporter.notifyWarning("Unknown cue tag: <" + tagName + ">");
                break;
        }

        checkNesting(current, tagName);

        return new CueElemData(tagName, EMPTY_CLASSES, "");
    }

    private void endTag(CueTreeNode current) {
        // N/A
    }

    private void checkNesting(CueTreeNode current, String tag) {
        if (current.findParentByTag(tag) != null) {
            reporter.notifyWarning("Nested <" + tag + "> tag not allowed");
        }
    }
}
