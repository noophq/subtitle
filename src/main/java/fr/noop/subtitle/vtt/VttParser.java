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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.noop.subtitle.base.BaseSubtitleParser;
import fr.noop.subtitle.base.CueData;
import fr.noop.subtitle.base.CueElemData;
import fr.noop.subtitle.base.CuePlainData;
import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.model.ValidationIssue;
import org.apache.commons.lang3.StringUtils;

import fr.noop.subtitle.model.SubtitleLine;
import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitlePlainText;
import fr.noop.subtitle.util.SubtitleStyle;
import fr.noop.subtitle.util.SubtitleStyledText;
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

    public VttParser(Charset charset) {
        this.charset = charset;
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
                notifyError(msg, 0);
            }
            foundEvent = CursorStatus.STYLE;
        } else if (line.startsWith(COMMENT_START)) {
            foundEvent = CursorStatus.NOTE;
        } else if (line.startsWith(REGION_START)) {
            if (cues) {
                String msg = REGION_START + " inside cues";
                notifyError(msg, 0);
            }
            foundEvent = CursorStatus.REGION;
        } else if (line.contains(ARROW)) {
            foundEvent = CursorStatus.CUE_TIMECODE;
        } else if (line.contains(ARROW)) {
            foundEvent = CursorStatus.CUE_ID;
        } else if (!line.trim().isEmpty()) {
            foundEvent = CursorStatus.CUE_ID;
        } else {
            foundEvent = CursorStatus.EMPTY_LINE;
        }

        return foundEvent;
    }

    @Override
    public VttObject parse(InputStream is, int subtitleOffset, int maxDuration, boolean strict) throws IOException, SubtitleParsingException {
        // Create vtt object
        VttObject vttObject = new VttObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
        CursorStatus cursorStatus = CursorStatus.NONE;
        VttCue cue = null;
        boolean shouldIgnoreCurrentCue = false;
        StringBuilder cueText = new StringBuilder(); // Text of the cue
        int lineNr = 1;
        boolean cuesBlock = false;

        String textLine = br.readLine();
        if (textLine == null || !WEBVTT.matcher(textLine).matches()) {
            String msg = String.format("Invalid WEBVTT header", textLine);
            notifyError(msg, lineNr);
        }

        while ((textLine = br.readLine()) != null) {
            // All Vtt files start with WEBVTT

            CursorStatus event = getNextEvent(textLine, cuesBlock);
            switch (event) {
                case CUE_TIMECODE:
                    cuesBlock = true;
                    break;

                case NOTE:
                    break;

                case REGION:
                    break;

                case STYLE:
                    break;

                case EMPTY_LINE:
                    break;
            }

            if (cursorStatus == CursorStatus.EMPTY_LINE) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // New cue
                cue = new VttCue();
                shouldIgnoreCurrentCue = false;
                cursorStatus = CursorStatus.CUE_ID;
                int arrowStart = textLine.indexOf(ARROW);
                if (arrowStart == -1) {
                    // First textLine is the cue number
                    cue.setId(textLine);
                    continue;
                }

                // There is no cue number
            }


            // Second textLine defines the start and end time codes
            // 00:01:21.456 --> 00:01:23.417
            if (cursorStatus == CursorStatus.CUE_ID) {
                int arrowStart = textLine.indexOf(ARROW);
                if (arrowStart == -1) {
                    String msg = String.format("Timecode textLine is badly formated: %s", textLine);
                    notifyError(msg, lineNr);
                }
                try {
                    cue.setStartTime(this.parseTimeCode(textLine.substring(0, arrowStart - 1), subtitleOffset));
                    cue.setEndTime(this.parseTimeCode(textLine.substring(arrowStart + 4), subtitleOffset));
                    if (cue.getStartTime().getTime() > maxDuration - subtitleOffset || cue.getEndTime().getTime() > maxDuration - subtitleOffset) {
                        shouldIgnoreCurrentCue = true;
                    }
                } catch (InvalidParameterException e) {
                    shouldIgnoreCurrentCue = true;
                }
                cursorStatus = CursorStatus.CUE_TIMECODE;
                continue;
            }

            // Following lines are the cue lines
            if (cursorStatus == CursorStatus.CUE_TIMECODE || cursorStatus == CursorStatus.CUE_TEXT) {
                if (cueText.length() > 0) {
                    // New line
                    cueText.append('\n');
                }

                cueText.append(textLine);
                cursorStatus = CursorStatus.CUE_TEXT;

                // If not strict, accept empty subtitle
                if (textLine.isEmpty()) {
                    if (!strict) {
                        cue.setTree(parseCueTree(cueText, 0));

                        if (!shouldIgnoreCurrentCue) {
                            vttObject.addCue(cue);
                        } else {
                            shouldIgnoreCurrentCue = false;
                        }
                        cue = null;
                        cueText.setLength(0);
                        cursorStatus = CursorStatus.EMPTY_LINE;
                    } else {
                        String msg = String.format("Empty subtitle is not allowed in WebVTT for cue at timecode: %s", cue.getStartTime());
                        notifyError(msg, lineNr);
                    }
                }
                continue;
            }

            if (cursorStatus == CursorStatus.CUE_TEXT && textLine.isEmpty()) {
                // End of cue
                // Process multilines text in one time
                // A class or a style can be applied for more than one line

                //cue.setLines(parseCueText(cueText));
                cue.setTree(parseCueTree(cueText, 0));

                if (!shouldIgnoreCurrentCue) {
                    vttObject.addCue(cue);
                } else {
                    shouldIgnoreCurrentCue = false;
                }
                cue = null;
                cueText.setLength(0);
                cursorStatus = CursorStatus.EMPTY_LINE;
                continue;
            }

            String msg = String.format("Unexpected line: %s", textLine);
            notifyError(msg, lineNr);
        }

        return vttObject;
    }

    protected CueTreeNode parseCueTree(StringBuilder cueText, int lineNr) throws SubtitleParsingException {
        CueTreeNode tree = new CueTreeNode();
        CueTreeNode current = tree;
        TagStatus tagStatus = TagStatus.NONE;
        int startTag = -1;
        int startText = -1;

        // Process:
        // - voice
        // - class
        // - styles
        int i = 0;
        while (i < cueText.length()) {
            char c = cueText.charAt(i);

            if (c == '<') {
                if (tagStatus == TagStatus.OPEN || tagStatus == TagStatus.CLOSE) {
                    String msg = "Invalid character inside a tag";
                    notifyError(msg, lineNr);
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
                    String msg = "Invalid character outside a tag";
                    notifyError(msg, lineNr);
                }

                // Close tag
                if (tagStatus == TagStatus.OPEN) {
                    // create cue element
                    String tag = cueText.substring(startTag, endTag);
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
                        notifyWarning(msg, lineNr);
                    }
                    // move up
                    current = current.getParent();
                }
                startText = i + 1;
                tagStatus = TagStatus.NONE;

            } else {
                if (startText < 0) {
                    startText = i;
                }
            }
            i++;
        }

        if (tagStatus != TagStatus.NONE) {
            notifyWarning("Disclosed tag", lineNr);
        }

        // Add last accumulated plain text if any
        if (startText >= 0 && startText < cueText.length() - 1) {
            String text = cueText.substring(startText, cueText.length());
            CueTreeNode plainChild = new CueTreeNode(new CuePlainData(text));
            current.add(plainChild);
        }

        if (current != tree) {
            notifyWarning("Missing close tags", lineNr);
        }
        return tree;
    }

    private List<SubtitleLine> parseCueText(String cueText) {
        String text = "";
        List<String> tags = new ArrayList<>();
        List<SubtitleLine> cueLines = new ArrayList<>();
        VttLine cueLine = null; // Current cue line

        // Process:
        // - voice
        // - class
        // - styles
        for (int i = 0; i < cueText.length(); i++) {
            String tag = null;
            TagStatus tagStatus = TagStatus.NONE;
            char c = cueText.charAt(i);

            if (c != '\n') {
                // Remove this newline from text
                text += c;
            }

            // Last characters (3 characters max)
            String textEnd = text.substring(Math.max(0, text.length() - 3), text.length());

            if (textEnd.equals("<b>") || textEnd.equals("<u>") || textEnd.equals("<i>") ||
                    textEnd.equals("<v ") || textEnd.equals("<c.") || textEnd.equals("<c ")) {
                // Open tag
                tag = String.valueOf(textEnd.charAt(1));
                tagStatus = TagStatus.OPEN;

                // Add tag
                tags.add(tag);

                // Remove open tag from text
                text = text.substring(0, text.length() - 3);
            } else if (c == '>') {
                // Close tag
                tagStatus = TagStatus.CLOSE;

                // Pop tag from tags
                tag = tags.remove(tags.size() - 1);

                int closeTagLength = 1; // Size in chars of the close tag

                if (textEnd.charAt(0) == '/') {
                    // Real close tag: </u>, </c>, </b>, </i>
                    closeTagLength = 4;
                }

                // Remove close tag from text
                text = text.substring(0, text.length() - closeTagLength);
            } else if (c != '\n' && i < cueText.length() - 1) {
                continue;
            }

            if (c != '\n' && text.isEmpty()) {
                // No thing todo
                continue;
            }

            if (cueLine == null) {
                cueLine = new VttLine();
            }

            // Create text, apply styles and append to the cue line
            SubtitleStyle style = new SubtitleStyle();
            List<String> analyzedTags = new ArrayList<>();
            analyzedTags.addAll(tags);

            if (tagStatus == TagStatus.CLOSE) {
                // Apply style from last close tag
                analyzedTags.add(tag);
            } else if (tagStatus == TagStatus.OPEN) {
                analyzedTags.remove(tags.size() - 1);
            }

            for (String analyzedTag : analyzedTags) {
                if (analyzedTag.equals("v")) {
                    cueLine.setVoice(text);
                    text = "";
                    break;
                }

                // Bold characters
                if (analyzedTag.equals("b")) {
                    style.setProperty(SubtitleStyle.Property.FONT_WEIGHT, SubtitleStyle.FontWeight.BOLD);
                    continue;
                }

                // Italic characters
                if (analyzedTag.equals("i")) {
                    style.setProperty(SubtitleStyle.Property.FONT_STYLE, SubtitleStyle.FontStyle.ITALIC);
                    continue;
                }

                // Underline characters
                if (analyzedTag.equals("u")) {
                    style.setProperty(SubtitleStyle.Property.TEXT_DECORATION, SubtitleStyle.TextDecoration.UNDERLINE);
                    continue;
                }

                // Class apply to characters
                if (analyzedTag.equals("c")) {
                    // Cannot convert class
                    if (tagStatus == TagStatus.CLOSE && tag.equals("c") && !textEnd.equals("/c>")) {
                        // This is not a real close tag
                        // so push it again
                        text = "";
                        tags.add(tag);
                    }

                    continue;
                }
            }

            if (!text.isEmpty()) {
                if (style.hasProperties()) {
                    cueLine.addText(new SubtitleStyledText(text, style));
                } else {
                    cueLine.addText(new SubtitlePlainText(text));
                }
            }

            if (c == '\n' || i == (cueText.length() - 1)) {
                // Line is finished
                cueLines.add(cueLine);
                cueLine = null;
            }

            text = "";
        }

        return cueLines;
    }

    protected SubtitleTimeCode parseTimeCode(String timeCodeString, int subtitleOffset) throws SubtitleParsingException {
        try {
            int separatorCount = StringUtils.countMatches(timeCodeString, ":");
            int minute;
            int second;
            int millisecond;

            if (separatorCount > 1) {
                // hh:mm:ss.ms
                int offset = timeCodeString.indexOf(":");
                int hour = Integer.parseInt(timeCodeString.substring(0, offset));
                String buffer = timeCodeString.substring(offset + 1);
                offset = buffer.indexOf(":");
                minute = Integer.parseInt(buffer.substring(0, offset));
                buffer = buffer.substring(offset + 1);
                offset = buffer.indexOf(".");
                second = Integer.parseInt(buffer.substring(0, offset));
                buffer = buffer.substring(offset + 1);
                millisecond = Integer.parseInt(buffer);
            } else {
                // mm:ss.ms
                int offset = timeCodeString.indexOf(":");
                minute = Integer.parseInt(timeCodeString.substring(0, offset));
                String buffer = timeCodeString.substring(offset + 1);
                offset = buffer.indexOf(".");
                second = Integer.parseInt(buffer.substring(0, offset));
                buffer = buffer.substring(offset + 1);
                millisecond = Integer.parseInt(buffer);
            }
            return new SubtitleTimeCode(0, minute, second, millisecond, subtitleOffset);

        } catch (NumberFormatException e) {
            String msg = String.format("Unable to parse time code: %s", timeCodeString);
            notifyError(msg, 0);
            throw e;
        }
    }

}