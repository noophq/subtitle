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

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleLine;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleRegionCue;
import fr.noop.subtitle.model.SubtitleStyled;
import fr.noop.subtitle.model.SubtitleText;
import fr.noop.subtitle.model.SubtitleWriterWithFrameRate;
import fr.noop.subtitle.model.SubtitleWriterWithOffset;
import fr.noop.subtitle.model.SubtitleWriterWithTimecode;

import fr.noop.subtitle.model.SubtitleWriterWithHeader;
import fr.noop.subtitle.util.SubtitleStyle;
import fr.noop.subtitle.util.SubtitleTimeCode;
import fr.noop.subtitle.util.SubtitleRegion.VerticalAlign;
import fr.noop.subtitle.util.SubtitleStyle.FontStyle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttWriter implements SubtitleWriterWithHeader, SubtitleWriterWithTimecode, SubtitleWriterWithFrameRate, SubtitleWriterWithOffset {
    private String charset; // Charset used to encode file
    private String outputTimecode;
    private String outputFrameRate;
    private String outputOffset;
    private String headerText; // header to append.

    public VttWriter(String charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        try {
            SubtitleTimeCode startTimeCode = new SubtitleTimeCode(0);
            float frameRate = 25;
            if (subtitleObject.hasProperty(SubtitleObject.Property.START_TIMECODE_PRE_ROLL)){
                startTimeCode = (SubtitleTimeCode) subtitleObject.getProperty(SubtitleObject.Property.START_TIMECODE_PRE_ROLL);
            }
            if (subtitleObject.hasProperty(SubtitleObject.Property.FRAME_RATE)) {
                frameRate = (float) subtitleObject.getProperty(SubtitleObject.Property.FRAME_RATE);
            }

            // Write header
            os.write(("WEBVTT\n").getBytes(this.charset));
            if (headerText != null){
                os.write(headerText.getBytes(this.charset));
            }
            os.write("\n".getBytes(this.charset));
            // Write cues
            for (SubtitleCue cue : subtitleObject.getCues()) {
                if (cue.getId() != null) {
                    // Write number of subtitle
                    String number = String.format("%s\n", cue.getId());
                    os.write(number.getBytes(this.charset));
                }

                // Write Start time and end time
                SubtitleTimeCode startTC = cue.getStartTime().convertWithOptions(startTimeCode, outputTimecode, frameRate, outputFrameRate, outputOffset);
                SubtitleTimeCode endTC = cue.getEndTime().convertWithOptions(startTimeCode, outputTimecode, frameRate, outputFrameRate, outputOffset);

                String vp = this.verticalPosition(cue);
                String startToEnd = this.formatTimeCode(startTC) + " --> " + this.formatTimeCode(endTC) + (vp != "" ? " " : "") + vp + "\n";
                os.write(startToEnd.getBytes(this.charset));

                // Write text
                //String text = String.format("%s\n", cue.getText());

                String text = "";
                for (SubtitleLine line : cue.getLines()) {
                    for (SubtitleText inText : line.getTexts()) {
                        String textString = inText.toString();
                        textString = textString.replace("&amp;", "&").replace("&", "&amp;"); // avoid writing "&amp;amp;" when replacing
                        textString = textString.replace("<", "&lt;");
                        textString = textString.replace(">", "&gt;");
                        if (inText instanceof SubtitleStyled) {
                            SubtitleStyle style = ((SubtitleStyled)inText).getStyle();
                            if (style.getFontStyle() == FontStyle.ITALIC || style.getFontStyle() == FontStyle.OBLIQUE) {
                                textString = String.format("<i>%s</i>", textString);
                            }
                            if (style.getColor() != null){
                                textString = String.format("<c.%s>%s</c>", style.getColor(), textString);
                            }
                        }
                        text += textString;
                    }
                    text += "\n";
                }
                os.write(text.getBytes(this.charset));

                // Write empty line
                os.write("\n".getBytes(this.charset));

                // Get region

            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Encoding error in input subtitle");
        }
    }

    private String verticalPosition(SubtitleCue cue) {
        if (cue instanceof SubtitleRegionCue) {
            VerticalAlign va = ((SubtitleRegionCue) cue).getRegion().getVerticalAlign();
            if (va == VerticalAlign.TOP) {
                // there is a bug in Shaka 4.X when aligned to top using "line:0"
                return "line:0.01%";
            }
            else {
                return "";
            }
        }
        return "";
    }
    private String formatTimeCode(SubtitleTimeCode timeCode) {
        return String.format("%02d:%02d:%02d.%03d",
                timeCode.getHour(),
                timeCode.getMinute(),
                timeCode.getSecond(),
                timeCode.getMillisecond());
    }

    @Override
    public void setTimecode(String timecode) {
        this.outputTimecode= timecode;
    }

    @Override
    public void setFrameRate(String frameRate) {
        this.outputFrameRate = frameRate;
    }

    @Override
    public void setOffset(String offset) {
        this.outputOffset = offset;
    }

    @Override
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
        
    }
}
