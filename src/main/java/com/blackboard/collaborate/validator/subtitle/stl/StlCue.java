/*
 * Title: StlCue
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.stl;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleLine;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleRegionCue;
import com.blackboard.collaborate.validator.subtitle.util.SubtitlePlainText;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleRegion;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleStyle;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleStyledText;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTextLine;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 30/09/15.
 */
public class StlCue extends BaseSubtitleCue implements SubtitleRegionCue {
    // List of TTI blocks
    // In most of the case there is only one TTI per cue
    // But very long cues are stored on many TTI blocks
    private final List<StlTti> ttis = new ArrayList<>();

    // Cues can be displayed vertically on the screen
    // Let's consider that the screen has a height and a width of 100%
    // Cue region has always these properties:
    // - x: 0
    // - width: 100
    // height and y values vary depending on TTI vp value and cue number of lines
    @Getter
    @Setter
    private SubtitleRegion region;

    @Getter
    private List<SubtitleLine> lines; // Lines composed of texts

    public StlCue(StlTti tti) {
        super(tti.getTci(), tti.getTco());
        this.lines = new ArrayList<>();
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

        for (String tfPart: tf.split("\u008a")) {
            SubtitleTextLine line = new SubtitleTextLine();
            SubtitleStyle textStyle = null;
            String text = null;
            boolean startText = false; // Set to true to start ingesting text
            int cIndex = 0; // Char index

            while (cIndex < tfPart.length()) {
                // Current char and current byte
                char cc = tfPart.charAt(cIndex);
                int cByte = (cc & 0xff);
                cIndex++;

                // If not defined, create new text with new style
                if (text == null) {
                    text = "";
                    textStyle = new SubtitleStyle();

                    // Start ingesting text before start box directive (0x0b)
                    startText = (tti.getJc() == StlTti.Jc.NONE);
                }

                // Start box directive
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

                // TODO: Process text decoration
                if (cByte == 0x80 || cByte == 0x82 || cByte == 0x84) {
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

                    if (!text.isEmpty()) {
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

            // Add text row
            if (!line.isEmpty()) {
                this.lines.add(line);
            }
        }
    }

    public boolean isEmpty() {
        return this.getText().isEmpty();
    }

    @Override
    public String getText() {
        StringBuilder bld = new StringBuilder();

        for (SubtitleLine line : lines) {
            bld.append(line.toString()).append("\n");
        }

        return bld.toString();
    }
}
