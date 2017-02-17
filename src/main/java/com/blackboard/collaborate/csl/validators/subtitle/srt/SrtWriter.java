/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.srt;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleWriter;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by clebeaupin on 02/10/15.
 */
public class SrtWriter implements SubtitleWriter {
    private Charset charset; // Charset used to encode file

    public SrtWriter(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, charset)) {
            int subtitleIndex = 0;

            for (SubtitleCue cue : subtitleObject.getCues()) {
                subtitleIndex++;

                // Write number of subtitle
                writer.write(String.format("%d", subtitleIndex));
                writer.write("\n");

                // Write Start time and end time
                writer.write(this.formatTimeCode(cue.getStartTime()));
                writer.write(" --> ");
                writer.write(this.formatTimeCode(cue.getEndTime()));
                writer.write("\n");

                // Write text
                writer.write(cue.getText());
                writer.write("\n");
                // Write emptyline
                writer.write("\n");
            }
        }
    }

    private String formatTimeCode(SubtitleTimeCode timeCode) {
        return String.format("%02d:%02d:%02d,%03d",
                timeCode.getHour(),
                timeCode.getMinute(),
                timeCode.getSecond(),
                timeCode.getMillisecond());
    }
}
