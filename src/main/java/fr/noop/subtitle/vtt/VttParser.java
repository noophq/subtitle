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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

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
public class VttParser implements SubtitleParser {
	
    private enum CursorStatus {
        NONE,
        SIGNATURE,
        EMPTY_LINE,
        CUE_ID,
        CUE_TIMECODE,
        CUE_TEXT;
    }

    private enum TagStatus {
        NONE,
        OPEN,
        CLOSE
    }

    private String charset; // Charset of the input files

    public VttParser(String charset) {
        this.charset = charset;
    }

    @Override
    public VttObject parse(InputStream is) throws IOException, SubtitleParsingException {
    	return parse(is, 0, true);
    }
    
    @Override
    public VttObject parse(InputStream is, boolean strict) throws IOException, SubtitleParsingException {
    	return parse(is, 0, true);
    }
    
    @Override
    public VttObject parse(InputStream is, int subtitleOffset, boolean strict) throws IOException, SubtitleParsingException {
	return parse(is, subtitleOffset, -1, strict);
    }
    
    @Override
    public VttObject parse(InputStream is, int subtitleOffset, int maxDuration, boolean strict) throws IOException, SubtitleParsingException {
        // Create srt object
        VttObject vttObject = new VttObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
        String textLine = "";
        CursorStatus cursorStatus = CursorStatus.NONE;
        VttCue cue = null;
        boolean shouldIgnoreCurrentCue = false;
        String cueText = ""; // Text of the cue

        while ((textLine = br.readLine()) != null) {
            textLine = textLine.trim();

            // All Vtt files start with WEBVTT
            if (cursorStatus == CursorStatus.NONE && textLine.equals("WEBVTT")) {
                cursorStatus = CursorStatus.SIGNATURE;
                continue;
            }

            if (cursorStatus == CursorStatus.SIGNATURE ||
                    cursorStatus == CursorStatus.EMPTY_LINE) {
                if (textLine.isEmpty()) {
                    continue;
                }

                // New cue
                cue = new VttCue();
                shouldIgnoreCurrentCue = false;
                cursorStatus = CursorStatus.CUE_ID;
                int arrowStart = textLine.indexOf("-->");
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
        	int arrowStart = textLine.indexOf("-->");
                if (arrowStart == -1) {
                    throw new SubtitleParsingException(String.format(
                            "Timecode textLine is badly formated: %s", textLine));
                }
                try {
                    cue.setStartTime(this.parseTimeCode(textLine.substring(0, arrowStart-1), subtitleOffset));
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
            if (cursorStatus == CursorStatus.CUE_TIMECODE ||
                            cursorStatus ==  CursorStatus.CUE_TEXT) {
                if (!cueText.isEmpty()) {
                    // New line
                    cueText += "\n";
                }

                cueText += textLine;
                cursorStatus = CursorStatus.CUE_TEXT;
                
                // If not strict, accept empty subtitle
                if (textLine.isEmpty()) {
					if (!strict) {
						cue.setLines(parseCueText(cueText));
						if (!shouldIgnoreCurrentCue) {
						    vttObject.addCue(cue);
						} else {
						    shouldIgnoreCurrentCue = false;
						}
						cue = null;
						cueText = "";
						cursorStatus = CursorStatus.EMPTY_LINE;
					} else {
			        	throw new SubtitleParsingException(String.format(
			        			"Empty subtitle is not allowed in WebVTT for cue at timecode: %s", cue.getStartTime()));
					}
				}
                continue;
            }

            if (cursorStatus == CursorStatus.CUE_TEXT && textLine.isEmpty()) {
                // End of cue
                // Process multilines text in one time
                // A class or a style can be applied for more than one line
                cue.setLines(parseCueText(cueText));
                if (!shouldIgnoreCurrentCue) {
                    vttObject.addCue(cue);
                } else {
		    shouldIgnoreCurrentCue = false;
		}
                cue = null;
                cueText = "";
                cursorStatus = CursorStatus.EMPTY_LINE;
                continue;
            }

        	throw new SubtitleParsingException(String.format(
        			"Unexpected line: %s", textLine));
        }

        return vttObject;
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
        for (int i=0; i<cueText.length(); i++) {
            String tag = null;
            TagStatus tagStatus = TagStatus.NONE;
            char c = cueText.charAt(i);

            if (c != '\n') {
                // Remove this newline from text
                text += c;
            }

            // Last characters (3 characters max)
            String textEnd = text.substring(Math.max(0, text.length()-3), text.length());

            if (textEnd.equals("<b>") || textEnd.equals("<u>") || textEnd.equals("<i>") ||
                    textEnd.equals("<v ") || textEnd.equals("<c.") || textEnd.equals("<c ")) {
                // Open tag
                tag = String.valueOf(textEnd.charAt(1));
                tagStatus = TagStatus.OPEN;

                // Add tag
                tags.add(tag);

                // Remove open tag from text
                text = text.substring(0, text.length()-3);
            } else if (c == '>') {
                // Close tag
                tagStatus = TagStatus.CLOSE;

                // Pop tag from tags
                tag = tags.remove(tags.size()-1);

                int closeTagLength = 1; // Size in chars of the close tag

                if (textEnd.charAt(0) == '/') {
                    // Real close tag: </u>, </c>, </b>, </i>
                    closeTagLength = 4;
                }

                // Remove close tag from text
                text = text.substring(0, text.length()-closeTagLength);
            } else if (c != '\n' && i < cueText.length()-1){
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

            for (String analyzedTag: analyzedTags) {
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

            if (c == '\n' || i == (cueText.length()-1)) {
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
            if (separatorCount > 1) {
        	// hh:mm:ss.ms
        	int offset = timeCodeString.indexOf(":");
        	int hour = Integer.parseInt(timeCodeString.substring(0, offset));
        	String buffer = timeCodeString.substring(offset+1);
        	offset = buffer.indexOf(":");
        	int minute = Integer.parseInt(buffer.substring(0, offset));
        	buffer = buffer.substring(offset+1);
        	offset = buffer.indexOf(".");
        	int second = Integer.parseInt(buffer.substring(0, offset));
        	buffer = buffer.substring(offset+1);
        	int millisecond = Integer.parseInt(buffer);
        	return new SubtitleTimeCode(hour, minute, second, millisecond, subtitleOffset);
            } else {
        	// mm:ss.ms
        	int offset = timeCodeString.indexOf(":");
        	int minute = Integer.parseInt(timeCodeString.substring(0, offset));
        	String buffer = timeCodeString.substring(offset+1);
        	offset = buffer.indexOf(".");
        	int second = Integer.parseInt(buffer.substring(0, offset));
        	buffer = buffer.substring(offset+1);
        	int millisecond = Integer.parseInt(buffer);
        	return new SubtitleTimeCode(0, minute, second, millisecond, subtitleOffset);
            }
        } catch (NumberFormatException e) {
            throw new SubtitleParsingException(String.format(
                    "Unable to parse time code: %s", timeCodeString));
        }
    }

}