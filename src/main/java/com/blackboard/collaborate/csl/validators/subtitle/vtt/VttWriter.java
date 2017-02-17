/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.vtt;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleWriter;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttWriter implements SubtitleWriter {
    private final Writer writer;

    public VttWriter(OutputStream outputStream, Charset charset) {
        this.writer = new OutputStreamWriter(outputStream, charset);
    }

    @Override
    public void write(SubtitleObject subtitleObject) throws IOException {
        // Write header
        writer.write(VttParser.WEBVTT_TAG);
        writer.write("\n\n");

        // Write regions
        for (Object obj : subtitleObject.getObjects()) {
            if (obj instanceof SubtitleCue) {
                writeCue((SubtitleCue) obj);
            } else {
                writer.write(obj.toString());
            }
            writer.write("\n");
        }
    }

    private void writeCue(SubtitleCue cue) throws IOException {
        if (cue.getId() != null) {
            // Write number of subtitle
            writer.write(cue.getId());
            writer.write("\n");
        }

        // Write Start time and end time
        writer.write(formatTimeCode(cue.getStartTime()));
        writer.write(" ");
        writer.write(VttParser.ARROW);
        writer.write(" ");
        writer.write(formatTimeCode(cue.getEndTime()));

        // TODO: write VTT cue settings if any
        Iterable<Map.Entry<String, String>> settings = cue.getSettings();
        if (settings != null) {
            for (Map.Entry<String, String> entry : settings) {
                writer.write(' ');
                writer.write(entry.getKey());
                writer.write(":");
                writer.write(entry.getValue());
            }
        }

        writer.write("\n");

        // Write text
        writer.write(cue.getText());
    }

    private static String formatTimeCode(SubtitleTimeCode timeCode) {
        int hours = timeCode.getHour();
        if (hours == 0) {
            return String.format("%02d:%02d.%03d",
                    timeCode.getMinute(),
                    timeCode.getSecond(),
                    timeCode.getMillisecond());
        }
        else {
            return String.format("%02d:%02d:%02d.%03d",
                    timeCode.getHour(),
                    timeCode.getMinute(),
                    timeCode.getSecond(),
                    timeCode.getMillisecond());
        }
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
