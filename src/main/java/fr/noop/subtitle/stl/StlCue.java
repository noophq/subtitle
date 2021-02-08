/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.stl;

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.model.SubtitleRegionCue;
import fr.noop.subtitle.util.*;
import fr.noop.subtitle.util.SubtitleStyle.FontStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 30/09/15.
 */
public class StlCue extends BaseSubtitleCue implements SubtitleRegionCue {
    // List of TTI blocks
    // In most of the case there is only one TTI per cue
    // But very long cues are stored on many TTI blocks
    private List<StlTti> ttis = new ArrayList<StlTti>();

    // Cues can be displayed vertically on the screen
    // Let's consider that the screen has a height and a width of 100%
    // Cue region has always these properties:
    // - x: 0
    // - width: 100
    // height and y values vary depending on TTI vp value and cue number of lines
    SubtitleRegion region;
    StlGsi.Dsc dsc;

    public StlCue(StlTti tti, StlGsi gsi) {
        super(tti.getTci(), tti.getTco());
        this.dsc = gsi.getDsc();
        this.addTti(tti);
    }

    public List<StlTti> getTtis() {
        return this.ttis;
    }

    public void addTti(StlTti tti) {
        this.ttis.add(tti);
        this.addText(tti);
    }

    /**
     * Build cue text from tti text field
     */
    private void addText(StlTti tti) {
        String tf = tti.getTf();
        this.setCharacterCodes(tf);

        for (String tfPart: tf.split("\u008a")) {
            SubtitleTextLine line = new SubtitleTextLine();
            SubtitleStyle textStyle = null;
            String text = null;
            boolean startText = true; // Set to true to start ingesting text
            int cIndex = 0; // Char index

            while (cIndex < tfPart.length()) {
                // Current char and current byte
                char cc = tfPart.charAt(cIndex);
                int cByte = (cc & 0xff); // get unsigned byte
                cIndex++;

                // If not defined, create new text with new style
                if (text == null) {
                    text = new String();
                    textStyle = new SubtitleStyle();
                    if (dsc == StlGsi.Dsc.DSC_TELETEXT_LEVEL_1 || dsc == StlGsi.Dsc.DSC_TELETEXT_LEVEL_2){ // teletext case
                        if (tti.getJc() == StlTti.Jc.NONE) {
                            // Start ingesting text before start box directive (0x0b)
                            startText = true;
                        } else {
                            startText = false;
                        }
                    }
                }

                // Start box directive // teletext case
                if (cByte == 0x0b) {
                    startText = true;
                }

                // Do not process these values
                if ((cByte >= 0x08 && cByte <= 0x09) ||
                        (cByte >= 0x0b && cByte <= 0x0f) ||
                        (cByte >= 0x18 && cByte <= 0x1f) ||
                        (cByte >= 0x86 && cByte <= 0x8f)) {
                    continue;
                }

                // FIXME: Process text decoration
                if (cByte == 0x80 || cByte == 0x82 || cByte == 0x84) {
                    startText = true;
                    if (cByte == StlTti.TextStyle.ITALIC_ON.getValue()) {
                        textStyle.setFontStyle(FontStyle.ITALIC);
                    }
                    if (cByte == StlTti.TextStyle.ITALIC_OFF.getValue()) {
                        textStyle.getProperties().remove(SubtitleStyle.Property.FONT_STYLE);
                    }
                    continue;
                }

                // Color information
                if ((cByte >= 0x00 && cByte <= 0x07) ||
                        (cByte >= 0x10 && cByte <= 0x17)) {
                    textStyle.setColor(StlTti.TextColor.getEnum(cByte).getColor());
                    continue;
                }

                // Text has not been initialized
                if (text == null) {
                    continue;
                }

                // Text content is closed
                if (cByte == 0x0a || cByte == 0x81 || cByte == 0x83 || cByte == 0x85) {
                    if (!textStyle.hasProperties()) {
                        // Style override any properties
                        // So do not register it
                        textStyle = null;
                    }

                    if (text != null && !text.isEmpty()) {
                        if (textStyle == null) {
                            line.addText(new SubtitlePlainText(text));
                        } else {
                            line.addText(new SubtitleStyledText(text, textStyle));
                        }
                    }

                    text = null;
                    textStyle = null;
                    continue;
                }

                // Readable char
                if (startText) {
                    text += cc;
                }
            }
            // if line not added before; add it
            if (line.isEmpty() && text != null){
                if (textStyle == null) {
                    line.addText(new SubtitlePlainText(text));
                } else {
                    line.addText(new SubtitleStyledText(text, textStyle));
                }
            }
            // Add text row
            if (!line.isEmpty()) {
                this.addLine(line);
            }
        }
    }

    public boolean isEmpty() {
        return this.getText().isEmpty();
    }

    public SubtitleRegion getRegion() {
       return this.region;
    }

    public void setRegion(SubtitleRegion region) {
        this.region = region;
    }
}
